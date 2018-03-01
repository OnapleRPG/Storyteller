package com.ylinor.storyteller.action;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.access.ObjectiveDao;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.ObjectiveBean;
import com.ylinor.storyteller.data.handlers.ConfigurationHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class DialogAction {
    @Inject
    private ConfigurationHandler configurationHandler;
    @Inject
    private ObjectiveDao objectiveDao;

    /**
     * Retrieve a dialog by its id
     * @param index Id of the dialog
     * @return Dialog, if exists
     */
    public Optional<DialogBean> getDialog(int index){
        try {
            return Optional.of(configurationHandler.getDialogList().get(index));
        } catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
    }

    /**
     * Retrieve a dialog matching a trigger if player meet requirements
     * @param trigger Name of the NPC that triggers the dialog
     * @param playerName Name of the player
     * @return Optional dialog
     */
    public Optional<DialogBean> getDialogByTrigger(String trigger, String playerName){
        for(DialogBean dialog: configurationHandler.getDialogList()) {
            if (dialog.getTrigger().contains(trigger)) {
                String objective = dialog.getObjective();
                String[] orConditions = objective.split("\\|\\|");
                boolean orVerified = false;
                for (String orCondition : orConditions) {
                    String[] andConditions = orCondition.split("&&");
                    boolean andVerified = true;
                    for (String andCondition : andConditions) {
                        boolean verified = false;
                        Pattern pattern = Pattern.compile("([a-zA-Z0-9_$]+)(<|<=|==|>=|>)(\\d)");
                        Matcher matcher = pattern.matcher(andCondition);
                        if (matcher.find()) {
                            Optional<ObjectiveBean> objectiveBean = objectiveDao.getObjectiveByNameAndPlayer(matcher.group(1), playerName);
                            int objectiveValue = (objectiveBean.isPresent()) ? objectiveBean.get().getState() : 0;
                            int compareValue = Integer.parseInt(matcher.group(3));
                            switch (matcher.group(2)) {
                                case "<":
                                    verified = (objectiveValue < compareValue);
                                    break;
                                case "<=":
                                    verified = (objectiveValue <= compareValue);
                                    break;
                                case "==":
                                    verified = (objectiveValue == compareValue);
                                    break;
                                case ">=":
                                    verified = (objectiveValue >= compareValue);
                                    break;
                                case ">":
                                    verified = (objectiveValue > compareValue);
                                    break;
                            }
                        } else if (!objective.equals("")) {
                            Storyteller.getLogger().warn("Wrong objective argument : \"" + objective + "\"");
                        }
                        andVerified = verified && andVerified;
                    }
                    orVerified = orVerified || andVerified;
                }
                if (orVerified) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
}

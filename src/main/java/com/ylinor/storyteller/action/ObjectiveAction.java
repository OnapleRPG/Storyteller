package com.ylinor.storyteller.action;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.access.ObjectiveDao;
import com.ylinor.storyteller.data.beans.ObjectiveBean;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ObjectiveAction {
    @Inject
    private ObjectiveDao objectiveDao;

    /**
     * Edit an objective depending on the argument given
     * @param source Player who's objective is gonna be edited
     * @param arguments Arguments containing objective name and action
     */
    public void setObjective(Player source, String arguments) {
        String[] splittedArguments;
        //  Check that regex match the operation
        Pattern pattern = Pattern.compile("([a-zA-Z0-9_$]+)(-=|\\+=|=)(\\d)");
        Matcher matcher = pattern.matcher(arguments);
        if (!matcher.find()) {
            Storyteller.getLogger().warn("Wrong objective arguments : \"" + arguments + "\"");
            return;
        }
        //  Remember the type of operation
        boolean relative = false, positive = false;
        switch (matcher.group(2)) {
            case "=":
                relative = false;
                positive = false;
                break;
            case "+=":
                relative = true;
                positive = true;
                break;
            case "-=":
                relative = true;
                positive = false;
                break;
        }
        String objectiveName = matcher.group(1);
        int objectiveValue = Integer.parseInt(matcher.group(3));
        Optional<ObjectiveBean> optionalObjective = objectiveDao.getObjectiveByNameAndPlayer(objectiveName, source.getName());
        ObjectiveBean objective;
        //  Operate on the objective
        if (optionalObjective.isPresent()) {
            objective = optionalObjective.get();
        } else {
            objective = new ObjectiveBean(source.getName(), objectiveName, 0);
        }
        if (relative) {
            if (positive) {
                objective.setState(objective.getState() + objectiveValue);
            } else {
                objective.setState(objective.getState() - objectiveValue);
            }
        } else {
            objective.setState(objectiveValue);
        }
        if (optionalObjective.isPresent()) {
            objectiveDao.updateObjectiveState(objective);
        } else {
            objectiveDao.insertObjective(objective);
        }
    }
}

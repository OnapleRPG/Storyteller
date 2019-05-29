package com.onaple.storyteller.action;

import com.onaple.itemizer.Itemizer;
import com.onaple.storyteller.data.beans.Condition;
import com.onaple.storyteller.data.beans.DialogBean;
import com.onaple.storyteller.data.beans.ItemCondition;
import com.onaple.storyteller.data.handlers.ConfigurationHandler;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptException;
import java.util.Optional;

@Singleton
public class DialogAction {
    @Inject
    private ConfigurationHandler configurationHandler;
    @Inject
    private ObjectiveAction objectiveAction;
    @Inject
    private MiscellaneousAction miscellaneousAction;
    @Inject
    private KillCountAction killCountAction;

    /**
     * Retrieve a dialog by its id
     * @param index Id of the dialog
     * @return Dialog, if exists
     */
    public Optional<DialogBean> getDialog(String index){
       return configurationHandler.getDialogList().stream().filter(dialogBean -> dialogBean.getId().equals(index)).findFirst();
       /* for(DialogBean dialog : configurationHandler.getDialogList()) {
            if (dialog.getId().equals(index)) {
                return Optional.of(dialog);
            }
        }
        return Optional.empty();*/
    }

    /**
     * Retrieve a dialog matching a trigger if player meet requirements
     * @param trigger Name of the NPC that triggers the dialog
     * @param player Player concerned
     * @return Optional dialog
     */
    public Optional<DialogBean> getDialogByTrigger(String trigger, Player player){
          return configurationHandler.getDialogList().stream().filter(dialog ->
              hasConditions(player, dialog, trigger)
          ).findFirst();
        }

    private boolean hasConditions(Player player, DialogBean dialog,String trigger){
        Condition itemCondition = new ItemCondition();
    try {
           return dialog.getTrigger().contains(trigger) &&
                   objectiveAction.playerMatchesObjective(player, dialog.getObjective()) &&
                   itemCondition.test(player, dialog.getItemsNeeded()) &&
                   killCountAction.playerMatchesKillCount(player, trigger, dialog.getKillCount());
       } catch (ScriptException | NoSuchMethodException e) {
        Itemizer.getLogger().error("error while reading script condition",e);
       }
        return false;
    }
}

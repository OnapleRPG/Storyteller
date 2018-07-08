package com.onaple.storyteller.action;

import com.onaple.storyteller.data.beans.DialogBean;
import com.onaple.storyteller.data.handlers.ConfigurationHandler;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
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
        for(DialogBean dialog: configurationHandler.getDialogList()) {
            if (dialog.getTrigger().contains(trigger)) {
                String objective = dialog.getObjective();
                String itemsNeeded = dialog.getItemsNeeded();
                String killCountObjective = dialog.getKillCount();
                if (objectiveAction.playerMatchesObjective(player, objective) && miscellaneousAction.hasItems(player, itemsNeeded) && killCountAction.playerMatchesKillCount(player, trigger, killCountObjective)) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
}

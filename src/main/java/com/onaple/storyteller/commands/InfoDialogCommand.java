package com.onaple.storyteller.commands;

import com.onaple.storyteller.Storyteller;
import com.onaple.storyteller.data.beans.DialogBean;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;

public class InfoDialogCommand implements CommandExecutor {
    /**
     * Command that prints informations about a dialog
     * @param src Source of the command
     * @param args Arguments containing dialog id
     * @return Command result
     * @throws CommandException
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> dialogId = args.getOne("dialog");
        if (dialogId.isPresent()) {
            Optional<DialogBean> dialogOpt = Storyteller.getDialogAction().getDialog(dialogId.get());
            if (dialogOpt.isPresent()) {
                DialogBean dialog = dialogOpt.get();
                // Printing triggers
                String triggers = (dialog.getTrigger().size() > 0) ? "\nTriggers : " : "";
                for (String trigger: dialog.getTrigger()) {
                    triggers = triggers.concat(trigger + ", ");
                }
                String objectives = (dialog.getObjective().length() > 0) ? "\nObjectives : " + dialog.getObjective() : "";
                String items = (dialog.getItemsNeeded().length() > 0) ? "\nItems : " + dialog.getItemsNeeded() : "";
                String killCounts = (dialog.getKillCount().length() > 0) ? "\nKill counts : " + dialog.getKillCount() : "";
                Text dialogDescription = Text.builder("Dialog ").append(Text.builder(dialog.getId()).style(TextStyles.BOLD).build())
                        .append(Text.builder("\n" + dialog.getPages().size() + " pages").build())
                        .append(Text.builder(triggers).build())
                        .append(Text.builder(objectives).build())
                        .append(Text.builder(items).build())
                        .append(Text.builder(killCounts).build())
                        .build();
                src.sendMessage(dialogDescription);
            } else {
                src.sendMessage(Text.of("Dialog not found"));
            }
        }
        return CommandResult.empty();
    }
}

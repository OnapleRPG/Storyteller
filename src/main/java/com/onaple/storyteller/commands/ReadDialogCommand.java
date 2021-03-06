package com.onaple.storyteller.commands;

import com.onaple.storyteller.Storyteller;

import com.onaple.storyteller.action.DialogAction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;


import javax.inject.Inject;
import java.util.Optional;

public class ReadDialogCommand implements CommandExecutor {
    @Inject
    DialogAction dialogAction;

    public ReadDialogCommand() {
    }

    /**
     * Command that open a given dialog book
     * @param src Origin of the c ommand
     * @param args Arguments provided with the command
     * @return Command result state
     * @throws CommandException
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String dialogId;

        if (args.<String>getOne("dialog").isPresent())
            dialogId = args.<String>getOne("dialog").orElse("default");
        else {
            return CommandResult.empty();
        }
        Optional<Player> playerOpt = args.getOne("player");
        try {
           BookView bookView = Storyteller.getBookGenerator().generateDialog(dialogId);
           if(playerOpt.isPresent()){
               playerOpt.get().sendBookView(bookView);
           } else {
               if (src instanceof Player) {
                   ((Player) src).sendBookView(bookView);
                   return CommandResult.success();
               } else {
                   src.sendMessage(Text.of("Target must be a player"));
               }
           }
        }
        catch (IllegalArgumentException e){
            src.sendMessage(Text.of("Dialog not found"));
        }

        return CommandResult.empty();
    }
}

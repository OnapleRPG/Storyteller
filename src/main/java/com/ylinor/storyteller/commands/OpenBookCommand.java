package com.ylinor.storyteller.commands;

import com.ylinor.storyteller.Storyteller;

import com.ylinor.storyteller.action.DialogAction;
import com.ylinor.storyteller.data.beans.DialogBean;
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

public class OpenBookCommand implements CommandExecutor {
    @Inject
    DialogAction dialogAction;

    public OpenBookCommand() {
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
        int dialogId;

        if (args.getOne("dialog").isPresent())
            dialogId = args.<Integer>getOne("dialog").get();

        else {
            return CommandResult.empty();
        }
        Optional<Player> playerOpt = args.getOne("player");
       try {
           BookView bookView = Storyteller.getBookGenerator().generateDialog(dialogId);
           if(playerOpt.isPresent()){
               playerOpt.get().sendBookView(bookView);
           }else {
               if (src instanceof Player) {
                   ((Player) src).sendBookView(bookView);
                   return CommandResult.success();
               } else {
                   src.sendMessage(Text.of("Target must be a player"));
               }
           }
       }
        catch (Exception e){
                src.sendMessage(Text.of("Dialog not found"));
        }

        return CommandResult.empty();
    }
}

package com.ylinor.storyteller;

import com.ylinor.storyteller.data.beans.DialogBean;

import org.slf4j.Logger;
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
    public OpenBookCommand() {
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int dialogId;

        if (args.getOne("dialog").isPresent())
            dialogId = args.<Integer>getOne("dialog").get();
        else {
            return CommandResult.empty();
        }
        Optional<BookView> bookViewOptional = Storyteller.getBookGenerator().getDialog(dialogId);
        if (bookViewOptional.isPresent()) {
            if (src instanceof Player) {
                ((Player) src).sendBookView(bookViewOptional.get());
                return CommandResult.success();
            } else {
                src.sendMessage(Text.of("Target must be a player"));
            }
        } else {
            src.sendMessage(Text.of("Dialog not found"));
        }
        return CommandResult.empty();
    }
}

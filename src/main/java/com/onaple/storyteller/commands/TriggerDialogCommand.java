package com.onaple.storyteller.commands;

import com.onaple.storyteller.Storyteller;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class TriggerDialogCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<String> name = args.getOne("npcName");
        Optional<Player> playerOptional = args.getOne("player");
        if (name.isPresent()) {
            if (playerOptional.isPresent()) {
                if (Storyteller.getBookGenerator().displayBook(playerOptional.get(), name.get())) {
                    return CommandResult.success();
                } else {
                    src.sendMessage(Text.of("NPC name not found"));
                    return CommandResult.empty();
                }
            } else {
                if (src instanceof Player) {
                    Storyteller.getBookGenerator().displayBook((Player) src, name.get());
                    return CommandResult.success();
                } else {
                    src.sendMessage(Text.of("Target must be a player"));
                    return CommandResult.empty();
                }
            }
        } else {
            src.sendMessage(Text.of("NPC name must be specified"));
            return CommandResult.empty();
        }
    }
}

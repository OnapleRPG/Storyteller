package com.onaple.storyteller.commands;

import com.onaple.storyteller.Storyteller;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ReloadCommand implements CommandExecutor {
    public ReloadCommand() {
    }

    /**
     * Command that reloads configuration
     * @param args Arguments provided with the command
     * @return Command result state
     * @throws CommandException
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Storyteller.getLogger().info("Reloading storyteller configuration...");

        src.sendMessage(Text.builder("----------------------------").color(TextColors.RED).build());
        try {
            src.sendMessage(
                    Text.builder("Dialogues configuration reloaded.").color(TextColors.GREEN)
                    .append(Text.builder(" " + Storyteller.getInstance().loadConfig() + " ").color(TextColors.GOLD).build())
                    .append( Text.builder("dialogues loaded.").color(TextColors.GREEN).build()).build()
            );

        } catch (ObjectMappingException e) {
            Text.builder("Dialogues configuration failed.").color(TextColors.DARK_RED);
            Text.builder(e.getMessage()).color(TextColors.RED);
        }


        src.sendMessage(Text.builder("----------------------------").color(TextColors.RED).build());

        return CommandResult.success();
    }
}

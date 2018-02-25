package com.ylinor.storyteller.commands;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.handlers.ConfigurationHandler;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;

import java.util.Optional;

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
        Storyteller.getInstance().loadConfig();
        return CommandResult.success();
    }
}

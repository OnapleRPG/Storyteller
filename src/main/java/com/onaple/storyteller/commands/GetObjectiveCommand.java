package com.onaple.storyteller.commands;

import com.onaple.storyteller.data.access.ObjectiveDao;
import com.onaple.storyteller.Storyteller;
import com.onaple.storyteller.data.beans.ObjectiveBean;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class GetObjectiveCommand implements CommandExecutor {
    @Inject
    ObjectiveDao objectiveDao;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> player = args.<Player>getOne("player");


        if(player.isPresent()) {
            List<ObjectiveBean> list = Storyteller.getObjectiveDao().getObjectiveByPlayer(player.get().get(Keys.DISPLAY_NAME).orElse(Text.of("Unknown")).toPlain());
            src.sendMessage(Text.builder().append(Text.builder("-------").color(TextColors.GREEN).build())
                                .append(Text.builder(src.getName() + "'s objectives").color(TextColors.RED).build())
                                .append(Text.builder("-------").color(TextColors.GREEN).build()).build());
            if (list.isEmpty()) {
                src.sendMessage(Text.builder("This player don't have any objectives yet.").color(TextColors.RED).build());
            }
            for (ObjectiveBean obj: list) {
                src.sendMessage(Text.builder(obj.getObjective()).color(TextColors.GOLD)
                                    .append(Text.builder(" : ").color(TextColors.GOLD).build())
                                    .append(Text.builder(""+ obj.getState()).color(TextColors.GREEN).build()).build());
            }
            src.sendMessage(Text.builder("---------------------").color(TextColors.GREEN).build());
            return CommandResult.success();
        } else {
            src.sendMessage(Text.builder("Missing player Argument").color(TextColors.RED).build());
            return CommandResult.empty();
        }
    }
}

package com.onaple.storyteller.commands;

import com.onaple.storyteller.Storyteller;
import com.onaple.storyteller.data.beans.ObjectiveBean;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

public class SetObjectivesCommand implements CommandExecutor
{

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> player = args.<Player>getOne("player");
        Optional<String> objective = args.getOne("objective");
        Optional<Integer> value = args.getOne("value");

        if(player.isPresent()){
            if(objective.isPresent()){
                if(value.isPresent()){
                    List<ObjectiveBean> objectiveByPlayer = Storyteller.getObjectiveDao().getObjectiveByPlayer(player.get().getName());
                    Optional<ObjectiveBean> objectiveBeanOptional = objectiveByPlayer.stream()
                            .filter(objectiveBean -> objectiveBean.getObjective().equals(objective.get())).findFirst();
                    if(!objectiveBeanOptional.isPresent()){
                        ObjectiveBean obj = new ObjectiveBean(player.get().getName(),objective.get(),value.get());
                        Storyteller.getObjectiveDao().insertObjective(obj);
                    } else {
                            ObjectiveBean objectiveToUpdate = objectiveBeanOptional.get();
                            objectiveToUpdate.setState(value.get());
                            Storyteller.getObjectiveDao().updateObjectiveState(objectiveToUpdate);
                    }
                    src.sendMessage(Text.builder().append(Text.of("The Objective "))
                            .append(Text.builder(objective.get()).color(TextColors.RED).build())
                            .append(Text.of(" has been set to "))
                            .append(Text.builder(""+value.get()).color(TextColors.GOLD).build())
                            .append(Text.of(" for the player "))
                            .append(Text.builder(""+player.get().getName()).color(TextColors.GOLD).build()).build());
                    return CommandResult.success();
                }
                src.sendMessage(Text.builder("The parameter <value> not present").color(TextColors.RED).build());
                return CommandResult.empty();
            }
            src.sendMessage(Text.builder("The parameter <objective> not present").color(TextColors.RED).build());
            return CommandResult.empty();
        }
        src.sendMessage(Text.builder("The parameter <player> not present").color(TextColors.RED).build());
        return CommandResult.empty();


    }
}

package com.ylinor.storyteller;


import com.ylinor.storyteller.action.DialogAction;
import com.ylinor.storyteller.commands.GetObjectiveCommand;
import com.ylinor.storyteller.commands.OpenBookCommand;
import com.ylinor.storyteller.commands.ReloadCommand;
import com.ylinor.storyteller.commands.SetObjectivesCommand;
import com.ylinor.storyteller.data.access.KillCountDao;
import com.ylinor.storyteller.data.access.ObjectiveDao;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.handlers.ConfigurationHandler;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.inject.Inject;

import java.nio.file.Path;
import java.util.Optional;


@Plugin(id = "storyteller", name = "Storyteller", version = "0.0.1")
public class Storyteller {
    private static Storyteller instance;
    public static Storyteller getInstance() {
        return instance;
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    private ConfigurationHandler configurationHandler;

    public void loadConfig() {
        configurationHandler.readDialogsConfiguration(configurationHandler.loadConfiguration(defaultConfig + "/dialogs.conf"));
    }


    private static ObjectiveDao objectiveDao;
    @Inject
    private void setObjectiveDao(ObjectiveDao objectiveDao) {
        Storyteller.objectiveDao = objectiveDao;
    }
    public static ObjectiveDao getObjectiveDao() {
        return objectiveDao;
    }

    @Inject
    private KillCountDao killCountDao;

    private static BookGenerator bookGenerator;

    private static Logger logger;
    @Inject
    private void setLogger(Logger logger) {
        Storyteller.logger = logger;
    }
    public static Logger getLogger() {
        return logger;
    }

    @Inject
    private void setBookGenerator(BookGenerator bookGenerator){ this.bookGenerator = bookGenerator; }
    public static BookGenerator getBookGenerator(){
        return bookGenerator;
    }
    @Inject
    private DialogAction dialogAction;

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        instance = this;
        loadConfig();
        objectiveDao.createTableIfNotExist();
        killCountDao.createTableIfNotExist();

        CommandSpec dialogSpec = CommandSpec.builder()
                .description(Text.of("Open book command"))
                .permission("storyteller.command.read")
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("dialog"))))
                .executor(new OpenBookCommand())
                .build();
        CommandSpec reloadSpec = CommandSpec.builder()
                .description(Text.of("Reload storyteller configuration"))
                .permission("storyteller.command.reload")
                .executor(new ReloadCommand())
                .build();

        CommandSpec getObjectiveSpec = CommandSpec.builder()
                .description(Text.of("Get player's actual objectives state"))
                .permission("storyteller.command.objectives")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new GetObjectiveCommand())
                .build();

        CommandSpec setObjectiveSpec = CommandSpec.builder()
                .description(Text.of("set player's objectives state"))
                .permission("storyteller.command.objectives")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))
                        ,GenericArguments.onlyOne(GenericArguments.string(Text.of("objective")))
                        ,GenericArguments.onlyOne(GenericArguments.integer(Text.of("value"))))
                .executor(new SetObjectivesCommand())
                .build();

        Sponge.getCommandManager().register(this, dialogSpec, "dialog");
        Sponge.getCommandManager().register(this, reloadSpec, "reload-storyteller");
        Sponge.getCommandManager().register(this, getObjectiveSpec, "get-objectives");
        Sponge.getCommandManager().register(this, setObjectiveSpec, "set-objective");
        logger.info("STORYTELLER initialized.");
    }

    /**
     * Handle villager interaction for dialog
     * @param event Event fired
     * @param player Player who originated the event
     */
    @Listener
    public void onInteract(InteractEntityEvent.Secondary event, @Root Player player) {
        Entity entity = event.getTargetEntity();
        Optional<Text> name = entity.get(Keys.DISPLAY_NAME);
        String entityType = entity.getType().getName();
        if (entityType.equals("villager") && name.isPresent()) {
            Optional<DialogBean> dialog = dialogAction.getDialogByTrigger(name.get().toPlain(), player);
            if (dialog.isPresent()) {
                BookView bookView = bookGenerator.generateDialog(dialog.get());
                player.sendBookView(bookView);
                event.setCancelled(true);
            } else {
                BookView bookView = bookGenerator.generateDefaultBook(player);
                player.sendBookView(bookView);
                event.setCancelled(true);
            }

        }
    }

    /**
     * Handle entity death for kill counter
     * @param event Event fired
     */
    @Listener
    public void onEntityDeath(DestructEntityEvent.Death event) {
        final Entity entity = event.getTargetEntity();
        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
        if (optDamageSource.isPresent()) {
            EntityDamageSource damageSource = optDamageSource.get();
            Entity killer = damageSource.getSource();
            if (killer instanceof Player) {
                Player player = (Player) killer;
                String entityName;
                if (entity.get(Keys.DISPLAY_NAME).isPresent()) {
                    entityName = entity.get(Keys.DISPLAY_NAME).get().toPlain();
                } else {
                    entityName = entity.getType().getName();
                }
                killCountDao.incrementKillCount(player.getName(), entityName);
            }
        }
    }

    /**
     * Get the current world
     * @return the world
     */
    public static World getWorld(){
        Optional<World> worldOptional = Sponge.getServer().getWorld("world");
        if(worldOptional.isPresent()){
            return worldOptional.get();
        }
        return null;
    }
}

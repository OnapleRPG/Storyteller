package com.onaple.storyteller;


import com.onaple.storyteller.action.DialogAction;
import com.onaple.storyteller.commands.*;
import com.onaple.storyteller.data.access.ObjectiveDao;
import com.onaple.storyteller.data.access.KillCountDao;
import com.onaple.storyteller.data.handlers.ConfigurationHandler;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Plugin(id = "storyteller", name = "Storyteller", version = "1.4")
public class Storyteller {
    private static Storyteller instance;
    public static Storyteller getInstance() {
        return instance;
    }

    @Inject
    @ConfigDir(sharedRoot=true)
    private Path configDir;




    @Inject
    private ConfigurationHandler configurationHandler;

    public int loadConfig() throws ObjectMappingException {
          initDefaultConfig();
          return configurationHandler.readDialogsConfiguration(configurationHandler.loadConfiguration(configDir + "/storyteller/"));
    }

    private void initDefaultConfig(){
        if (Files.notExists(Paths.get(configDir+"/storyteller/"))){
            Optional<PluginContainer> pluginContainer = Sponge.getPluginManager().getPlugin("storyteller");
            if (pluginContainer.isPresent()) {
                Optional<Asset> dialogDefaultConfigFile = pluginContainer.get().getAsset("example.conf");
                getLogger().info("No dialogs files found in /config/storryteller, a default config will be loaded");
                if (dialogDefaultConfigFile.isPresent()) {
                    try {
                        dialogDefaultConfigFile.get().copyToDirectory(Paths.get(configDir+"/storyteller/"));
                    } catch (IOException e) {
                        logger.error("Error while initializing default config : ".concat(e.getMessage()));
                    }
                } else {
                    logger.warn("Default config not found.");
                }
            } else {
                logger.warn("Plugin was not able to reference itself !");
            }
        }
    }



    private static ObjectiveDao objectiveDao;
    @Inject
    private void setObjeExceptionctiveDao(ObjectiveDao objectiveDao) {
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
    private void setDialogAction(DialogAction dialogAction) {
        Storyteller.dialogAction = dialogAction;
    }
    public static DialogAction getDialogAction() {
        return dialogAction;
    }
    private static DialogAction dialogAction;

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        instance = this;
        try {
          getLogger().info("Dialogs configuration loaded. " +loadConfig()+ " dialogs loaded");
        } catch (ObjectMappingException e) {
            logger.error("Error while loading dialogs configuration : " + e.getMessage());
        }
        objectiveDao.createTableIfNotExist();
        killCountDao.createTableIfNotExist();

        CommandSpec reloadSpec = CommandSpec.builder()
                .description(Text.of("Reload storyteller configuration"))
                .permission("storyteller.command.reload")
                .executor(new ReloadCommand())
                .build();
        Sponge.getCommandManager().register(this, reloadSpec, "reload-storyteller");

        CommandSpec getObjectiveSpec = CommandSpec.builder()
                .description(Text.of("Get player's actual objectives state"))
                .permission("storyteller.command.objectives")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new GetObjectiveCommand())
                .build();
        CommandSpec setObjectiveSpec = CommandSpec.builder()
                .description(Text.of("Set player's actual objectives state"))
                .permission("storyteller.command.objectives")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))
                        ,GenericArguments.onlyOne(GenericArguments.string(Text.of("objective")))
                        ,GenericArguments.onlyOne(GenericArguments.integer(Text.of("value"))))
                .executor(new SetObjectivesCommand())
                .build();
        CommandSpec objectiveSpec = CommandSpec.builder()
                .description(Text.of("Get and set player objectives"))
                .child(getObjectiveSpec, "get", "g")
                .child(setObjectiveSpec, "set", "s")
                .build();
        Sponge.getCommandManager().register(this, objectiveSpec, "objective");

        CommandSpec readDialogSpec = CommandSpec.builder()
                .description(Text.of("Read a dialog with given id"))
                .permission("storyteller.command.dialog")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("dialog"))),
                        GenericArguments.optional(GenericArguments.player(Text.of("player"))))
                .executor(new ReadDialogCommand())
                .build();
        CommandSpec triggerDialogSpec = CommandSpec.builder()
                .description(Text.of("Trigger a dialog related to a NPC name"))
                .permission("storyteller.command.dialog")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("npcName"))),
                        GenericArguments.optional(GenericArguments.player(Text.of("player")))
                )
                .executor(new TriggerDialogCommand())
                .build();
        CommandSpec infoDialogSpec = CommandSpec.builder()
                .description(Text.of("Get information about a trigger by id"))
                .permission("storyteller.command.dialog")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("dialog"))))
                .executor(new InfoDialogCommand())
                .build();
        CommandSpec dialogSpec = CommandSpec.builder()
                .description(Text.of("Read and see informations about dialogs"))
                .child(readDialogSpec, "read", "r")
                .child(triggerDialogSpec, "trigger", "t")
                .child(infoDialogSpec, "info", "i")
                .build();
        Sponge.getCommandManager().register(this, dialogSpec, "dialog");

        logger.info("STORYTELLER initialized.");
    }

  /*  /**
     * Handle villager interaction for dialog
     * @param event Event fired
     * @param player Player who originated the event
     */
    /*@Listener
    public void onInteract(InteractEntityEvent.Secondary event, @Root Player player) {
        Entity entity = event.getTargetEntity();
        Optional<Text> name = entity.get(Keys.DISPLAY_NAME);
        String entityType = entity.getType().getName();
        if (name.isPresent()) {
            if (bookGenerator.displayBook(player,name.get().toPlain())) {
                event.setCancelled(true);
            }
        }
    }
*/
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
                Optional<Text> entityDisplayName = entity.get(Keys.DISPLAY_NAME);
                if (entityDisplayName.isPresent()) {
                    entityName = entityDisplayName.get().toPlain();
                } else {
                    entityName = entity.getType().getName();
                }
                killCountDao.incrementKillCount(player.getName(), entityName);
            }
        }
    }
}

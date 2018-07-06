package com.ylinor.storyteller.data.handlers;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.access.ObjectiveDao;
import com.ylinor.storyteller.data.beans.*;
import com.ylinor.storyteller.data.serializer.ActionSerializer;
import com.ylinor.storyteller.data.serializer.ButtonSerializer;
import com.ylinor.storyteller.data.serializer.DialogSerializer;
import com.ylinor.storyteller.data.serializer.PageSerializer;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class ConfigurationHandler {
    @Inject
    ObjectiveDao objectiveDao;

    public ConfigurationHandler() {}

    private  List<DialogBean> dialogList;
    public  List<DialogBean> getDialogList(){
        return dialogList;
    }

    /**
     * Read storyteller configuration and interpret it
     * @param configurationNodes ConfigurationNodes to read from
     */
    public int readDialogsConfiguration(List<CommentedConfigurationNode> configurationNodes) throws ObjectMappingException {
        dialogList = new ArrayList<>();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(DialogBean.class), new DialogSerializer());

        for (CommentedConfigurationNode configNode : configurationNodes) {
            for (Map.Entry<Object, ?> configEntry : configNode.getChildrenMap().entrySet()) {
                String configEntryKey = (String) configEntry.getKey();
                CommentedConfigurationNode configEntryNode = (CommentedConfigurationNode)configEntry.getValue();
                // Legacy configuration where "dialogs" is the root node
                if (configEntryKey.equals("dialogs")) {
                    dialogList.addAll(configEntryNode.getList(TypeToken.of(DialogBean.class)));
                } else {
                    // Current configuration mode where dialogs are by key-values
                    DialogBean dialog = configEntryNode.getValue(TypeToken.of(DialogBean.class));
                    dialog.setId(configEntryKey);
                    dialogList.add(dialog);
                }
            }
        }

        return dialogList.size();
    }

    /**
     * Load configuration from file
     * @param configName Name of the configuration in the configuration folder
     * @return Configuration ready to be used
     */
    public List<CommentedConfigurationNode> loadConfiguration(String configName) {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(ActionBean.class), new ActionSerializer());
        serializers.registerType(TypeToken.of(ButtonBean.class), new ButtonSerializer());
        serializers.registerType(TypeToken.of(PageBean.class), new PageSerializer());
        serializers.registerType(TypeToken.of(DialogBean.class), new DialogSerializer());
        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        List<CommentedConfigurationNode> commentedNodes = new ArrayList<>();
        List<Path> paths = new ArrayList<>();
        try(Stream<Path> pathStream = Files.walk(Paths.get(configName), 1)) {
            paths = pathStream.filter(f -> {
                String fn = f.getFileName().toString();
                return fn.endsWith(".conf");
            }).collect(Collectors.toList());
        } catch (IOException e) {
            Storyteller.getLogger().error(e.getMessage());
        }
        for (Path p: paths) {
           try {
               ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(p).build();

               CommentedConfigurationNode configNode = null;
               configNode = configLoader.load(options);
               commentedNodes.add(configNode);
           }  catch (IOException e) {
                Storyteller.getLogger().error("Error while loading configuration '" + p + "' : " + e.getMessage());
            }
        }


        return commentedNodes;
    }
}

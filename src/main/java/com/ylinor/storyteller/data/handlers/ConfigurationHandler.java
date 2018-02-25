package com.ylinor.storyteller.data.handlers;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationHandler {
    private ConfigurationHandler() {}

    private static List<DialogBean> dialogList;
    public static List<DialogBean> getDialogList(){
        return dialogList;
    }

    /**
     * Read storyteller configuration and interpret it
     * @param configurationNode ConfigurationNode to read from
     */
    public static void readDialogsConfiguration(CommentedConfigurationNode configurationNode){
        dialogList = new ArrayList<>();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(DialogBean.class), new DialogSerializer());
        try {
            dialogList = configurationNode.getNode("dialogs").getList(TypeToken.of(DialogBean.class));
        } catch (ObjectMappingException e) {
            Storyteller.getLogger().error("Error while reading configuration 'storyteller' : " + e.getMessage());
        }
        Storyteller.getLogger().info(dialogList.size() + " dialogs loaded.");
    }

    /**
     * Load configuration from file
     * @param configName Name of the configuration in the configuration folder
     * @return Configuration ready to be used
     */
    public static CommentedConfigurationNode loadConfiguration(String configName) {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(ButtonBean.class), new ButtonSerializer());
        serializers.registerType(TypeToken.of(PageBean.class), new PageSerializer());
        serializers.registerType(TypeToken.of(DialogBean.class), new DialogSerializer());
        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configName)).build();
        CommentedConfigurationNode configNode = null;
        try {
            configNode = configLoader.load(options);
        } catch (IOException e) {
            Storyteller.getLogger().error("Error while loading configuration '" + configName + "' : " + e.getMessage());
        }
        return configNode;
    }
}

package com.ylinor.storyteller.serializer;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.data.ButtonBuilder;
import com.ylinor.storyteller.data.beans.ButtonBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ButtonSerializer implements TypeSerializer<ButtonBean> {
    @Override
    public ButtonBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        ButtonBuilder buttonBuilder = ButtonBean.builder();
        String text = value.getNode("text").getString();
        if(text != null){
            buttonBuilder.text(text);
        }
        String color = value.getNode("color").getString();
        if(color != null){
            buttonBuilder.color(color);
        }
        String action = value.getNode("action").getString();
        if(action != null){
            buttonBuilder.action(action);
        }
        String args = value.getNode("args").getString();
        if(args != null){
            buttonBuilder.args(args);
        }
        return buttonBuilder.build();
    }

    @Override
    public void serialize(TypeToken<?> type, ButtonBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}

package com.ylinor.storyteller.data.serializer;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.data.beans.ActionBean;
import com.ylinor.storyteller.data.beans.ButtonBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ButtonSerializer implements TypeSerializer<ButtonBean> {
    @Override
    public ButtonBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ButtonBean buttonBean = new ButtonBean();
        buttonBean.setText(value.getNode("text").getString());
        buttonBean.setColor(value.getNode("color").getString());
        buttonBean.setActions(value.getNode("actions").getList(TypeToken.of(ActionBean.class)));
        return buttonBean;
    }

    @Override
    public void serialize(TypeToken<?> type, ButtonBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}

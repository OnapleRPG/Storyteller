package com.ylinor.storyteller.data.serializer;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.data.beans.ActionBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ActionSerializer implements TypeSerializer<ActionBean> {
    @Override
    public ActionBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ActionBean actionBean = new ActionBean();
        actionBean.setName(value.getNode("name").getString());
        actionBean.setArg(value.getNode("arg").getString());
        return actionBean;
    }

    @Override
    public void serialize(TypeToken<?> type, ActionBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}

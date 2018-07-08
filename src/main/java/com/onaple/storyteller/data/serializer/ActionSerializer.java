package com.onaple.storyteller.data.serializer;

import com.google.common.reflect.TypeToken;
import com.onaple.storyteller.data.beans.ActionBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ActionSerializer implements TypeSerializer<ActionBean> {
    @Override
    public ActionBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ActionBean actionBean = new ActionBean();
        // Loading legacy configuration actions with separate name and arguments
        if (value.getNode("name").getString() != null) {
            actionBean.setName(value.getNode("name").getString());
            actionBean.setArg(value.getNode("arg").getString());
        } else {
            // Loading regular configuration actions
            String actionString = value.getString();
            String actionStrings[] = actionString.split(" ", 2);
            if (actionStrings.length >= 2) {
                actionBean.setName(actionStrings[0]);
                actionBean.setArg(actionStrings[1]);
            }
        }
        return actionBean;
    }

    @Override
    public void serialize(TypeToken<?> type, ActionBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}

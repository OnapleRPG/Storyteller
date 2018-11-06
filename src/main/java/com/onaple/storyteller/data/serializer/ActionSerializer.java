package com.onaple.storyteller.data.serializer;

import com.google.common.reflect.TypeToken;
import com.onaple.storyteller.data.ActionEnum;
import com.onaple.storyteller.data.beans.ActionBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.apache.commons.lang3.EnumUtils;

public class ActionSerializer implements TypeSerializer<ActionBean> {
    @Override
    public ActionBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        ActionBean actionBean = new ActionBean();
        // Loading legacy configuration actions with separate name and arguments
        if (value.getNode("name").getString() != null) {
            actionBean.setName(getActionFromName(value.getNode("name").getString()));
            actionBean.setArg(value.getNode("arg").getString());
        } else {
            // Loading regular configuration actions
            String actionString = value.getString();
            String actionStrings[] = actionString.split(" ", 2);
            if (actionStrings.length >= 2) {
                actionBean.setName(getActionFromName(actionStrings[0]));
                actionBean.setArg(actionStrings[1]);
            }
        }
        return actionBean;
    }

    @Override
    public void serialize(TypeToken<?> type, ActionBean obj, ConfigurationNode value) throws ObjectMappingException {

    }

    private ActionEnum getActionFromName(String name) throws ObjectMappingException {
        if (EnumUtils.isValidEnum(ActionEnum.class, name)) {
            return ActionEnum.valueOf(name);
        } else {
            throw new ObjectMappingException("Wrong Action name. Please refer to the action list");
        }
    }
}

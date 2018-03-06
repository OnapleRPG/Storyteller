package com.ylinor.storyteller.data.serializer;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class DialogSerializer implements TypeSerializer<DialogBean> {

    @Override
    public DialogBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        DialogBean dialogBean = new DialogBean(value.getNode("id").getInt());
        dialogBean.setPages(value.getNode("pages").getList(TypeToken.of(PageBean.class)));
        dialogBean.setTrigger(value.getNode("trigger").getList(TypeToken.of(String.class)));
        String objective = value.getNode("objective").getString();
        dialogBean.setObjective((objective != null) ? objective : "");
        String itemsNeeded = value.getNode("items").getString();
        dialogBean.setItemsNeeded((itemsNeeded != null) ? itemsNeeded : "");
        return dialogBean;
    }

    @Override
    public void serialize(TypeToken<?> type, DialogBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}

package com.ylinor.storyteller.data.serializer;

import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.PageBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class PageSerializer implements TypeSerializer<PageBean> {

    @Override
    public PageBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        PageBean pageBean = new PageBean();
        pageBean.setMessage(value.getNode("text").getString());
        pageBean.setButtonBeanList(value.getNode("buttons").getList(TypeToken.of(ButtonBean.class)));
        return pageBean;

    }

    @Override
    public void serialize(TypeToken<?> type, PageBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}

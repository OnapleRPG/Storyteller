package com.ylinor.storyteller.data.beans;

import java.util.ArrayList;
import java.util.List;

public class PageBean {
    private int id;
    private String message;
    private List<ButtonBean> buttonBeanList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ButtonBean> getButtonBeanList() {
        return buttonBeanList;
    }

    public void setButtonBeanList(List<ButtonBean> buttonBeanList) {
        this.buttonBeanList = buttonBeanList;
    }

    public PageBean() {
        this.buttonBeanList = new ArrayList<>();
    }
}

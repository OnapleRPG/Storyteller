package com.onaple.storyteller.data.beans;

import java.util.ArrayList;
import java.util.List;

public class ButtonBean {
    private int id;
    private String text;
    private String color;
    private List<ActionBean> actions;

    public ButtonBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<ActionBean> getActions() {
        return actions;
    }
    public void setActions(List<ActionBean> actions) {
        this.actions = actions;
    }

    public ButtonBean(String text, String color) {
        this.text = text;
        this.color = color;
        this.actions = new ArrayList<>();
    }
}

package com.ylinor.storyteller.data.beans;

import com.ylinor.storyteller.data.ButtonBuilder;

public class ButtonBean {
    private int id;
    private String text;
    private String color;
    private ActionEnum action;
    private String Arg;

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

    public ButtonBean(String text, String color, ActionEnum action, String arg) {
        this.text = text;
        this.color = color;
        this.action = action;
        Arg = arg;
    }

    public static ButtonBuilder builder(){
        return new ButtonBuilder();
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

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    public String getArg() {
        return Arg;
    }

    public void setArg(String arg) {
        Arg = arg;
    }
}

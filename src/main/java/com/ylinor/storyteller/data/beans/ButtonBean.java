package com.ylinor.storyteller.data.beans;

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

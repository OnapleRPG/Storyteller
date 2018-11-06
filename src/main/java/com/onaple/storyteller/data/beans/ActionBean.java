package com.onaple.storyteller.data.beans;


import com.onaple.storyteller.data.ActionEnum;

public class ActionBean {
    private ActionEnum name;
    private String arg;

    public ActionEnum getName() {
        return name;
    }
    public void setName(ActionEnum name) {
        this.name = name;
    }

    public String getArg() {
        return arg;
    }
    public void setArg(String arg) {
        this.arg = arg;
    }

    public ActionBean() {

    }
    public ActionBean(ActionEnum name, String arg) {
        this.name = name;
        this.arg = arg;
    }
}

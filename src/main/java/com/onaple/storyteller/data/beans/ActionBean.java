package com.onaple.storyteller.data.beans;

import java.util.ArrayList;
import java.util.List;


public class ActionBean {
    private String name;
    private String arg;

    public String getName() {
        return name;
    }
    public void setName(String name) {
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
    public ActionBean(String name, String arg) {
        this.name = name;
        this.arg = arg;
    }
}

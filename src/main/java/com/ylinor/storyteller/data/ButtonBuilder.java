package com.ylinor.storyteller.data;

import com.ylinor.storyteller.data.beans.ActionEnum;
import com.ylinor.storyteller.data.beans.ButtonBean;

public class ButtonBuilder {
    private String text = "Button";

    private String color = "RED";

    private ActionEnum action = null;

    private String args = "";
    public ButtonBuilder text(String text){
        this.text = text;
        return this;
    }
    public ButtonBuilder color(String color){
        this.color = color;
        return this;
    }
    public ButtonBuilder action(String action){
      this.action = ActionEnum.valueOf(action);
      return this;
    }
     public ButtonBuilder args(String args){
        this.args = args;
        return this;
     }

    public ButtonBuilder() {
    }

    public ButtonBean build(){
        return new ButtonBean(text,color,action,args);
    }
}

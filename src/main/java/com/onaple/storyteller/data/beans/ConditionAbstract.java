package com.onaple.storyteller.data.beans;

import org.spongepowered.api.entity.living.player.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @autor Hugo on 22/05/19.
 */
public abstract class ConditionAbstract implements Condition{

    ScriptEngine engine;

    protected String functionWrapper = "%s;" +
            "function cond1(object){" +
            "return %s;" +
            "}";

    public ConditionAbstract() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

}

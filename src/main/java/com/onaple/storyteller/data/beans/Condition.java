package com.onaple.storyteller.data.beans;

import org.spongepowered.api.entity.living.player.Player;

import javax.script.ScriptException;

/**
 * @autor Hugo on 22/05/19.
 */
public interface Condition {

    boolean test(Player player, String condition) throws ScriptException, NoSuchMethodException;

}

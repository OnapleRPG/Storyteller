package com.onaple.storyteller.data.beans;

import org.junit.Test;
import org.spongepowered.api.entity.living.player.Player;

import javax.script.ScriptException;

import static org.junit.Assert.*;

/**
 * @autor Hugo on 23/05/19.
 */
public class ItemConditionTest {

    @Test
    public void test1() throws ScriptException, NoSuchMethodException {
        ItemCondition itemCondition = new ItemCondition();

        assertTrue(itemCondition.test(null,"5==5"));
        assertFalse(itemCondition.test(null,"5>6"));
        assertTrue(itemCondition.test(null,"5<6 && ( test:t==2 || 2==2)"));
    }
}
package com.ylinor.storyteller.utils;

import java.util.regex.Matcher;

public class ConditionUtil {
    public static boolean matchesObjective(Matcher matcher, int objectiveValue, int compareValue) {
        boolean verified = false;
        switch (matcher.group(2)) {
            case "<":
                verified = (objectiveValue < compareValue);
                break;
            case "<=":
                verified = (objectiveValue <= compareValue);
                break;
            case "==":
                verified = (objectiveValue == compareValue);
                break;
            case ">=":
                verified = (objectiveValue >= compareValue);
                break;
            case ">":
                verified = (objectiveValue > compareValue);
                break;
        }
        return verified;
    }
}

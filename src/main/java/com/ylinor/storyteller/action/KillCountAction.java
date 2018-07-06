package com.ylinor.storyteller.action;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.access.KillCountDao;
import com.ylinor.storyteller.data.beans.KillCountBean;
import com.ylinor.storyteller.utils.ConditionUtil;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KillCountAction {
    @Inject
    private KillCountDao killCountDao;

    /**
     * Start a kill counter
     * @param player Player concerned
     * @param npcNames Name of the NPC giving the counter
     * @param monsterName Name or type of the monster
     */
    public void startKillCount(Player player, String npcNames, String monsterName) {
        Optional<KillCountBean> killCountBean = killCountDao.getKillCount(npcNames, player.getName(), monsterName);
        if (!killCountBean.isPresent()) {
            killCountDao.insertKillCount(new KillCountBean(npcNames, player.getName(), monsterName, 0));
        }
    }

    /**
     * Stop a kill counter
     * @param player Player concerned
     * @param npcNames Name of the NPC giving the counter
     * @param monsterName Name or type of the monster
     */
    public void stopKillCount(Player player, String npcNames, String monsterName) {
        killCountDao.deleteKillCount(player.getName(), player.getName(), monsterName);
    }

    /**
     * Returns true if the player kill counter exist and has reached a given amount
     * @param player Player the counter stands for
     * @param npcNames Name of the NPC giving the counter
     * @param condition Condition including a monster name and an amount
     * @return Boolean true if the count has bean reached
     */
    public boolean playerMatchesKillCount(Player player, String npcNames, String condition) {
        String playerName = player.getName();
        String[] orConditions = condition.split("\\|\\|");
        boolean orVerified = false;
        for (String orCondition : orConditions) {
            String[] andConditions = orCondition.split("&&");
            boolean andVerified = true;
            for (String andCondition : andConditions) {
                boolean verified = false;
                Pattern pattern = Pattern.compile("([a-zA-Z0-9_$]+)(<|<=|==|>=|>)(\\d)");
                Matcher matcher = pattern.matcher(andCondition);
                if (matcher.find()) {
                    Optional<KillCountBean> killCountBean = killCountDao.getKillCount(npcNames, player.getName(), matcher.group(1));
                    int objectiveValue = (killCountBean.isPresent()) ? killCountBean.get().getCount() : 0;
                    int compareValue = Integer.parseInt(matcher.group(3));
                    verified = ConditionUtil.matchesObjective(matcher, objectiveValue, compareValue);
                } else if (!condition.equals("")) {
                    Storyteller.getLogger().warn("Wrong killcount argument : \"" + condition + "\"");
                }
                andVerified = verified && andVerified;
            }
            orVerified = orVerified || andVerified;
        }
        return orVerified || condition.isEmpty();
    }
}

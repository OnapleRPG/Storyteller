package com.onaple.storyteller.data.beans;

public class KillCountBean {
    private int index;
    private String npcName;
    private String player;
    private String monsterName;
    private int count;

    public KillCountBean() {
    }

    public KillCountBean(String npcName, String player, String monsterName, int count) {
        this.npcName = npcName;
        this.player = player;
        this.monsterName = monsterName;
        this.count = count;
    }

    public KillCountBean(int index, String npcName, String player, String monsterName, int count) {
        this.index = index;
        this.npcName = npcName;
        this.player = player;
        this.monsterName = monsterName;
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String objective) {
        this.monsterName = monsterName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

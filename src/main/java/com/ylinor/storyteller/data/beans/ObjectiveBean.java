package com.ylinor.storyteller.data.beans;

public class ObjectiveBean {
    private int index;
    private String player;
    private String objective;
    private int state;

    public ObjectiveBean() {
    }

    public ObjectiveBean(int index, String player, String objective, int state) {
        this.index = index;
        this.player = player;
        this.objective = objective;
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

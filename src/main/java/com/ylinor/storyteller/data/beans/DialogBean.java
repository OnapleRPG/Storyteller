package com.ylinor.storyteller.data.beans;

import java.util.ArrayList;
import java.util.List;


public class DialogBean {

    private int id;
    private List<PageBean> pages;
    private List<String> trigger;
    private String objective;
    private String itemsNeeded;
    private String killCount;

    public List<String> getTrigger() {
        return trigger;
    }
    public void setTrigger(List<String> trigger) {
        this.trigger = trigger;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public List<PageBean> getPages() {
        return pages;
    }
    public void setPages(List<PageBean> pages) {
        this.pages = pages;
    }

    public String getObjective() {
        return objective;
    }
    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getItemsNeeded() {
        return itemsNeeded;
    }
    public void setItemsNeeded(String itemsNeeded) {
        this.itemsNeeded = itemsNeeded;
    }

    public String getKillCount() {
        return killCount;
    }
    public void setKillCount(String killCount) {
        this.killCount = killCount;
    }

    public DialogBean(int id) {
        this.id = id;
        this.pages = new ArrayList<>();
        this.trigger = new ArrayList<>();
    }


}

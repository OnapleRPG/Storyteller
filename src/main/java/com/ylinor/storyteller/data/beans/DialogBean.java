package com.ylinor.storyteller.data.beans;

import com.ylinor.storyteller.data.beans.dao.DialogDao;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class DialogBean {

    private int id;
    private List<PageBean> pages;

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

    public DialogBean(int id) {
        this.pages = new ArrayList<>();
    }


}

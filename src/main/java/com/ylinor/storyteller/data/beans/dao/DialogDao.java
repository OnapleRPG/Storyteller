package com.ylinor.storyteller.data.beans.dao;

import com.ylinor.storyteller.data.beans.DialogBean;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class DialogDao {
    private int index=0;
    private List<DialogBean> dialogList = new ArrayList<>();
    public Optional<DialogBean> getDialog(int index){
        try {
            return Optional.of(dialogList.get(index));
        } catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
    }
    public void addDialog(DialogBean dialogBean) {
        dialogList.add(dialogBean);
        index ++;
    }

    public int getIndex(){
        return index;
    }
}

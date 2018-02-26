package com.ylinor.storyteller.data.access;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class DialogDao {
   /* private int index=0;
    private List<DialogBean> dialogList = new ArrayList<>();
    public Optional<DialogBean> getDialog(int index){
        try {
            return Optional.of(dialogList.get(index));
        } catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
    }

    public void addDialog(DialogBean dialogBean) {
        dialogBean.setId(getIndex());
        dialogList.add(dialogBean);
        index ++;
    }

    public Optional<DialogBean> getDialogByTrigger(String trigger){
        return dialogList.stream().filter(dialogBean -> dialogBean.getTrigger().contains(trigger)).findFirst();
    }


    public int getIndex(){
        return index;
    }*/
}

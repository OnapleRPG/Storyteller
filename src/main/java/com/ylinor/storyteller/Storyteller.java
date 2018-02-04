package com.ylinor.storyteller;


import com.ylinor.storyteller.data.beans.ActionEnum;
import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import com.ylinor.storyteller.data.beans.dao.DialogDao;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Plugin(id = "storyteller", name = "Storyteller", version = "0.0.1")
public class Storyteller {

    @Inject
    private Logger logger;
    @Inject
    private BookGenerator bookGenerator;
    @Inject
    private  DialogDao dialogDao;


    @Listener
    public void onServerStart(GameInitializationEvent event) {
        ButtonBean buttonBean = new ButtonBean();
        buttonBean.setId(0);
        buttonBean.setText("click Me");
        buttonBean.setColor("RED");
        buttonBean.setAction(ActionEnum.OPEN_DIALOG);
        buttonBean.setArg("1");
        List<ButtonBean> buttons = new ArrayList<>();
        buttons.add(buttonBean);
        PageBean pageBean = new PageBean();
        pageBean.setMessage("lorem ipsum dolor sit amet");
        pageBean.setButtonBeanList(buttons);
        DialogBean dialog1 = new DialogBean(dialogDao.getIndex());
        List<PageBean> pages = new ArrayList<>();
        pages.add(pageBean);
        dialog1.setPages(pages);
        dialogDao.addDialog(dialog1);

        DialogBean dialog2 = new DialogBean(dialogDao.getIndex());
        PageBean page2 = new PageBean();
        page2.setMessage("deuxieme PAGE !!!!");
        dialog2.getPages().add(page2);

        dialogDao.addDialog(dialog2);

        logger.info("Storyteller Started");
    }

    @Listener
    public void onInteract(InteractEntityEvent.Secondary event, @Root Player player) {

        Entity entity = event.getTargetEntity();
       /* BookView bookView = BookView.builder()
                .title(Text.of("Story Mode"))
                .author(Text.of("Notch"))
                .addPage(Text.builder("There once was a Steve...").append(
                        Text.builder("Click here!")
                        .onClick(TextActions.runCommand("tell Spongesquad I'm ready!"))
                        .build()).build()).build();*/
       BookView bookView = bookGenerator.getDialog(dialogDao.getDialog(0).get());
        player.sendBookView(bookView);

        // do stuff
    }

}

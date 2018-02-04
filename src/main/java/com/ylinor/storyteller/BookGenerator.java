package com.ylinor.storyteller;

import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import com.ylinor.storyteller.data.beans.dao.DialogDao;

import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Singleton
public class BookGenerator {
    @Inject
    private Game game;
    @Inject
    private DialogDao dialogDao;
    @Inject
    private Logger logger;

    public BookView getDialog(DialogBean dialog){
        BookView.Builder bookviewBuilder = BookView.builder();
        for (PageBean pageBean : dialog.getPages()
             ) {
            bookviewBuilder.addPage(generatePage(pageBean));
        }
           return bookviewBuilder.build();
    }
    private Text generatePage(PageBean page){
        Text.Builder text = Text.builder(page.getMessage());
        if (!page.getButtonBeanList().isEmpty()) {
            List<ButtonBean> buttons = page.getButtonBeanList();
            for (ButtonBean buttonBean : buttons) {
            text.append(generateButton(buttonBean));
            }
        }
        return text.build();
    }

    private Text generateButton(ButtonBean buttonBean) {
        Text.Builder textBuilder = Text.builder(buttonBean.getText());
        Optional<TextColor> textColor = game.getRegistry().getType(TextColor.class,buttonBean.getColor().toUpperCase());
        if(textColor.isPresent()){
            textBuilder.color(textColor.get());
        }

        switch (buttonBean.getAction()) {
            case OPEN_DIALOG:
                textBuilder.onClick(TextActions.executeCallback(commandSource-> { changeDialog((Player)commandSource,Integer.parseInt(buttonBean.getArg())); }));
                break;
        }
        return textBuilder.build();
    }
    private void changeDialog(Player source, int dialogIndex) {
        Optional<DialogBean> dialogBeanOptional = dialogDao.getDialog(dialogIndex);
        if(dialogBeanOptional.isPresent()){
            source.sendBookView(getDialog(dialogBeanOptional.get()));
        } else {
            source.sendMessage(Text.builder("The dialog at the index : "+ dialogIndex + " can not be loaded.").color(TextColors.RED).build());
        }

    }
}

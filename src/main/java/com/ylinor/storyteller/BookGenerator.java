package com.ylinor.storyteller;

import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import com.ylinor.storyteller.data.access.DialogDao;

import org.spongepowered.api.Game;
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

@Singleton
public class BookGenerator {
    @Inject
    private Game game;
    @Inject
    private DialogDao dialogDao;

    public BookView getDialog(DialogBean dialog){
        BookView.Builder bookviewBuilder = BookView.builder();
        for (PageBean pageBean : dialog.getPages()
             ) {
            bookviewBuilder.addPage(generatePage(pageBean));
        }
           return bookviewBuilder.build();
    }

    public BookView getDefaultView(Player player){
        DialogBean dialogBean = new DialogBean(dialogDao.getIndex());
        PageBean pageBean =new PageBean();
        pageBean.setMessage("Grettings " + player.getName());
        dialogBean.getPages().add(pageBean);
        return getDialog(dialogBean);
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

    public Optional<BookView> getDialog(int dialogid){
        Optional<DialogBean> dialogBeanOptional = dialogDao.getDialog(dialogid);

        if (dialogBeanOptional.isPresent()) {
            BookView.Builder bookviewBuilder = BookView.builder();
            for (PageBean pageBean : dialogBeanOptional.get().getPages()
                    ) {
                bookviewBuilder.addPage(generatePage(pageBean));
            }
            return Optional.of(bookviewBuilder.build());
        }
        return Optional.empty();
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

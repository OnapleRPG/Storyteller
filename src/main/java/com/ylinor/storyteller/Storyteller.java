package com.ylinor.storyteller;


import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.access.DialogDao;
import com.ylinor.storyteller.data.handlers.ConfigurationHandler;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.inject.Inject;

import java.nio.file.Path;
import java.util.Optional;


@Plugin(id = "storyteller", name = "Storyteller", version = "0.0.1")
public class Storyteller {

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    private static BookGenerator bookGenerator;

    private static Logger logger;
    @Inject
    private void setLogger(Logger logger) {
        Storyteller.logger = logger;
    }
    public static Logger getLogger() {
        return logger;
    }

    @Inject
    private void setBookGenerator(BookGenerator bookGenerator){ this.bookGenerator= bookGenerator; }
    public static BookGenerator getBookGenerator(){
        return bookGenerator;
    }
    @Inject
    private DialogDao dialogDao;

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        ConfigurationHandler.readDialogsConfiguration(ConfigurationHandler.loadConfiguration(defaultConfig+""));

        for (DialogBean dialog: ConfigurationHandler.getDialogList()) {
            dialogDao.addDialog(dialog);
        }

        CommandSpec commandSpec = CommandSpec.builder()
                .description(Text.of("Open book command"))
                .permission("storyteller.command.read")
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("dialog"))))
                .executor(new OpenBookCommand())
                .build();

        Sponge.getCommandManager().register(this, commandSpec, "dialog");

       /* ButtonBean buttonBean = new ButtonBean();
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

        dialogDao.addDialog(dialog2);*/

        logger.info("STORYTELLER initialized.");
    }

    @Listener
    public void onInteract(InteractEntityEvent.Secondary event, @Root Player player) {

        Entity entity = event.getTargetEntity();
        Optional<Text> name = entity.get(Keys.DISPLAY_NAME);
        if (name.isPresent()) {
            logger.info(name.get().toPlain());
            Optional<DialogBean> dialog = dialogDao.getDialogByTrigger((name.get().toPlain()));
            if (dialog.isPresent()) {
                BookView bookView = bookGenerator.getDialog(dialog.get());
                player.sendBookView(bookView);
            } else {
                player.sendBookView(bookGenerator.getDefaultView(player));
            }

        }


       /* BookView bookView = BookView.builder()
                .title(Text.of("Story Mode"))
                .author(Text.of("Notch"))
                .addPage(Text.builder("There once was a Steve...").append(
                        Text.builder("Click here!")
                        .onClick(TextActions.runCommand("tell Spongesquad I'm ready!"))
                        .build()).build()).build();*/


        // do stuff
    }
    /**
     * Get the current world
     * @return the world
     */
    public static World getWorld(){
        Optional<World> worldOptional = Sponge.getServer().getWorld("world");
        if(worldOptional.isPresent()){
            return worldOptional.get();
        }
        return null;
    }
}

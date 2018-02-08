package com.ylinor.storyteller;


import com.google.common.reflect.TypeToken;
import com.ylinor.storyteller.data.beans.ActionEnum;
import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import com.ylinor.storyteller.data.access.DialogDao;
import com.ylinor.storyteller.serializer.ButtonSerializer;
import com.ylinor.storyteller.serializer.DialogSerializer;
import com.ylinor.storyteller.serializer.PageSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
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

import javax.inject.Inject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Plugin(id = "storyteller", name = "Storyteller", version = "0.0.1")
public class Storyteller {

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    private static BookGenerator bookGenerator;

    @Inject
    private Logger logger;
    @Inject
    private void setBookGenerator(BookGenerator bookGenerator){ this.bookGenerator= bookGenerator; }
    public static BookGenerator getBookGenerator(){
        return bookGenerator;
    }
    @Inject
    private DialogDao dialogDao;

    @Listener
    public void onServerStart(GameInitializationEvent event) {

        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(ButtonBean.class), new ButtonSerializer());
        serializers.registerType(TypeToken.of(PageBean.class), new PageSerializer());
        serializers.registerType(TypeToken.of(DialogBean.class), new DialogSerializer());
        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);

        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(defaultConfig).build();

        ConfigurationNode rootNode;
        List<DialogBean> dialogBeanList = new ArrayList<>();
        try {
            rootNode = loader.load(options);
            dialogBeanList = rootNode.getNode("dialogs").getList(TypeToken.of(DialogBean.class));
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.info("" + dialogBeanList.get(0).getTrigger().size());
        for (DialogBean dialogBean:dialogBeanList
             ) {
            dialogDao.addDialog(dialogBean);
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

        logger.info("Storyteller Started with " + dialogBeanList.size());
    }

    @Listener
    public void onInteract(InteractEntityEvent.Secondary event, @Root Player player) {

        Entity entity = event.getTargetEntity();
        Optional<Text> name = entity.get(Keys.DISPLAY_NAME);
        if (name.isPresent()) {
            logger.info(name.get().toPlain());
            Optional<DialogBean> d = dialogDao.getDialogByTrigger((name.get().toPlain()));
            if (d.isPresent()) {
                BookView bookView = bookGenerator.getDialog(d.get());
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

}

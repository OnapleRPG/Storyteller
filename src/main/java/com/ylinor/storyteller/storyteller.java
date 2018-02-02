package com.ylinor.storyteller;


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
import java.util.logging.Logger;

@Plugin(id = "storyteller", name = "Storyteller", version = "0.0.1")
public class storyteller {

    @Inject
    private Logger logger;
    @Listener
    public void onServerStart(GameInitializationEvent event) {
        logger.info("Storyteller Started");
    }
    @Listener
    public void onInteract(InteractEntityEvent.Secondary event, @Root Player player) {
        Entity entity = event.getTargetEntity();
        player.sendMessage(Text.of(entity.getType().toString()));
        BookView bookView = BookView.builder()
                .title(Text.of("Story Mode"))
                .author(Text.of("Notch"))
                .addPage(Text.builder("There once was a Steve...").append(
                        Text.builder("Click here!")
                        .onClick(TextActions.runCommand("tell Spongesquad I'm ready!"))
                        .build()).build()).build();

        player.sendBookView(bookView);

        // do stuff
    }

}

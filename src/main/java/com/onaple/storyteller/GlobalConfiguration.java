package com.onaple.storyteller;

import org.spongepowered.api.entity.EntityType;

import java.util.List;

public class GlobalConfiguration {
    private boolean interaction;
    private List<EntityType> interactibleEntities;

    public GlobalConfiguration(boolean interaction, List<EntityType> interactibleEntities) {
        this.interaction = interaction;
        this.interactibleEntities = interactibleEntities;
    }

    public boolean isInteraction() {
        return interaction;
    }

    public List<EntityType> getInteractibleEntities() {
        return interactibleEntities;
    }
}

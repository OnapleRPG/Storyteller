package com.ylinor.storyteller.action;

import com.flowpowered.math.vector.Vector3i;
import com.ylinor.itemizer.service.IItemService;
import com.ylinor.storyteller.Storyteller;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class MiscellaneousAction {

    /**
     * Execute a command
     * @param commandText Command line
     */
    public void executeCommand(Player source, String commandText) {
        Sponge.getCommandManager().process(source, commandText);
    }

    /**
     * Teleport the player to a given position
     * @param source Player to teleport
     * @param position Position in a string format
     */
    public void teleport(Player source, String position) {
        try{
            String pos[] = position.split(" ");
            Vector3i vector3i = new Vector3i(Integer.parseInt(pos[0]),Integer.parseInt(pos[1]),Integer.parseInt(pos[2]));
            Location<World> location = Storyteller.getWorld().getLocation(vector3i);
            source.setLocation(location);
        } catch (Exception e){
            source.sendMessage(Text.builder("The position : "+ position + " is invalid.").color(TextColors.RED).build());
            e.printStackTrace();
        }
    }

    /**
     * Give the player an item from Itemizer
     * @param source Player to give the item to
     * @param itemString Id or name of the item
     */
    public void giveItem(Player source, String itemString) {
        Optional<ItemStack> optionalItem = Optional.empty();
        try {
            int itemId = Integer.parseInt(itemString);
            Optional<IItemService> optionalIItemService = Sponge.getServiceManager().provide(IItemService.class);
            if (optionalIItemService.isPresent()) {
                IItemService iItemService = optionalIItemService.get();
                optionalItem = iItemService.retrieve(itemId);
            }
        } catch (NumberFormatException e) {
            Optional<ItemType> optionalType = Sponge.getRegistry().getType(ItemType.class, itemString);
            if (optionalType.isPresent()) {
                optionalItem = Optional.of(ItemStack.builder().itemType(optionalType.get()).build());
            }
        }
        if (optionalItem.isPresent()) {
            ItemStack item = optionalItem.get();
            if (source.getInventory().offer(item).getRejectedItems().size() > 0) {
                Extent extent = source.getLocation().getExtent();
                Entity entity = extent.createEntity(EntityTypes.ITEM, source.getLocation().getPosition());
                entity.offer(Keys.REPRESENTED_ITEM, item.createSnapshot());
                extent.spawnEntity(entity);
            }
        }
    }
}

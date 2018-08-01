package com.onaple.storyteller.action;

import com.flowpowered.math.vector.Vector3i;
import com.onaple.storyteller.Storyteller;
import com.onaple.itemizer.service.IItemService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Location<World> location = source.getWorld().getLocation(vector3i);
            source.setLocation(location);
        } catch (Exception e){
            source.sendMessage(Text.builder("The position : "+ position + " is invalid.").color(TextColors.RED).build());
            Storyteller.getLogger().error("Error while teleporting : " + e.getMessage());
        }
    }

    /**
     * Give the player an item from Itemizer
     * @param player Player to give the item to
     * @param itemString Id or name of the item, with optional quantity
     */
    public void giveItem(Player player, String itemString) {
        Optional<ItemStack> optionalItem = convertStringToItemStack(itemString);
        if (optionalItem.isPresent()) {
            ItemStack item = optionalItem.get();
            if (player.getInventory().offer(item).getRejectedItems().size() > 0) {
                Extent extent = player.getLocation().getExtent();
                Entity entity = extent.createEntity(EntityTypes.ITEM, player.getLocation().getPosition());
                entity.offer(Keys.REPRESENTED_ITEM, item.createSnapshot());
                extent.spawnEntity(entity);
            }
        }
    }

    /**
     * Remove item(s) from player inventory
     * @param player Player to take the item from
     * @param itemString Id or name of the item, with optional quantity
     */
    public void removeItem(Player player, String itemString) {
        Optional<ItemStack> optionalItem = convertStringToItemStack(itemString);
        if (optionalItem.isPresent()) {
            ItemStack item = optionalItem.get();
            player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(InventoryRow.class)).query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item)).poll(item.getQuantity());
        }
    }

    /**
     * Check that player has item
     * @param player Player who's inventory is checked
     * @param itemString Id or name of the item, with optional quantity
     */
    public boolean hasItem(Player player, String itemString) {
        Optional<ItemStack> optionalItem = convertStringToItemStack(itemString);
        if (optionalItem.isPresent()) {
            ItemStack item = optionalItem.get();
            return player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(InventoryRow.class)).contains(item);
        } else {
            return false;
        }
    }

    /**
     * Check that player has item(s)
     * @param player Player who's inventory is checked
     * @param condition Item conditions
     * @return Condition matched
     */
    public boolean hasItems(Player player, String condition) {
        String[] orConditions = condition.split("\\|\\|");
        boolean orVerified = false;
        for (String orCondition : orConditions) {
            String[] andConditions = orCondition.split("&&");
            boolean andVerified = true;
            for (String andCondition : andConditions) {
                boolean verified = false;
                Pattern pattern = Pattern.compile("([a-zA-Z0-9_$]+)( (\\d))?");
                Matcher matcher = pattern.matcher(andCondition);
                if (matcher.find()) {
                    verified = hasItem(player, andCondition);
                } else if (!condition.equals("")) {
                    Storyteller.getLogger().warn("Wrong item needed argument : \"" + condition + "\"");
                }
                andVerified = verified && andVerified;
            }
            orVerified = orVerified || andVerified;
        }
        return orVerified || condition.isEmpty();
    }

    /**
     * Try to convert a string into an itemstack
     * @param itemString String to convert
     * @return Optional of itemstack
     */
    private Optional<ItemStack> convertStringToItemStack(String itemString) {
        String[] itemStringSplitted = itemString.split(" ");
        Optional<ItemStack> optionalItem = Optional.empty();
        //  Getting item from string or integer, using Itemizer plugin
            String itemId = itemStringSplitted[0];
            Optional<IItemService> optionalIItemService = Sponge.getServiceManager().provide(IItemService.class);
            if (optionalIItemService.isPresent()) {
                IItemService iItemService = optionalIItemService.get();
                optionalItem = iItemService.retrieve(itemId);
            }
            if (!optionalItem.isPresent()) {
                Optional<ItemType> optionalType = Sponge.getRegistry().getType(ItemType.class, itemStringSplitted[0]);
                if (optionalType.isPresent()) {
                    optionalItem = Optional.of(ItemStack.builder().itemType(optionalType.get()).build());
                }
            }
        //  Taking multiple items into account
        int itemAmount = 1;
        if (itemStringSplitted.length >= 2) {
            try {
                itemAmount = Integer.parseInt(itemStringSplitted[1]);
            } catch (NumberFormatException e) {
                Storyteller.getLogger().warn("Wrong amount of item \"" + itemStringSplitted[0] + "\" in configuration.");
            }
        }
        if (optionalItem.isPresent()) {
            ItemStack item = optionalItem.get();
            item.setQuantity(itemAmount);
        }
        return optionalItem;
    }
}

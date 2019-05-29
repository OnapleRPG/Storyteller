package com.onaple.storyteller.data.beans;

import com.onaple.itemizer.Itemizer;
import com.onaple.itemizer.service.IItemService;
import com.onaple.storyteller.Storyteller;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.InventoryRow;

import javax.inject.Singleton;
import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @autor Hugo on 22/05/19.
 */
@Singleton
public class ItemCondition extends ConditionAbstract implements Condition{

    @Override
    public boolean test(Player player, String condition) throws ScriptException, NoSuchMethodException {
        if (condition.isEmpty()){
            return true;
        }
        String script = String.format(functionWrapper,"var utils = Java.type(\"com.onaple.storyteller.data.beans.ItemCondition\")",condition);
        script = applyMethod(script);
        engine.eval(script);
        Invocable invocable = (Invocable) engine;
        Itemizer.getLogger().info(script);
        boolean result = (boolean) invocable.invokeFunction("cond1",player);
        Itemizer.getLogger().info("{} returned from script {}",result,script);
        return result;
    }

    private String applyMethod(String script){
        Pattern itemPattern = Pattern.compile("([a-z]*:[a-z]*)",Pattern.MULTILINE);
        Matcher m = itemPattern.matcher(script);

        while (m.find()){
            String value = m.group();
            script = script.replace(value, "utils.getNumber(\"" + value+ "\",  object)");
        }
        return script;
    }


    public static int getNumber(String itemString, Player player) {
        Optional<ItemStack> optionalItem = convertStringToItemStack(itemString);

        if (optionalItem.isPresent()) {
            return player.getInventory()
                    .query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(optionalItem.get())).totalItems();
        }
        return 0;
    }

    public static Optional<ItemStack> convertStringToItemStack(String itemString) {
        String[] itemStringSplitted = itemString.split(":");
        Optional<ItemStack> optionalItem = Optional.empty();

        String itemScope = itemStringSplitted[0];
        String itemId = itemStringSplitted[1];
        if(itemScope.equals(Itemizer.getInstance().getId())) {
            Optional<IItemService> optionalIItemService = Sponge.getServiceManager().provide(IItemService.class);
            if (optionalIItemService.isPresent()) {
                IItemService iItemService = optionalIItemService.get();
                optionalItem = iItemService.retrieve(itemId);
            } else {
                Storyteller.getLogger().warn("Itemizer service reference not found");
            }
        } else {
            Optional<ItemType> optionalType = Sponge.getRegistry().getType(ItemType.class, itemString);
            if (optionalType.isPresent()) {
                optionalItem = Optional.of(ItemStack.builder().itemType(optionalType.get()).build());
            }
        }

        return optionalItem;
    }
}

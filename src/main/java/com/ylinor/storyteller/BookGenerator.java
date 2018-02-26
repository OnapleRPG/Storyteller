package com.ylinor.storyteller;

import com.flowpowered.math.vector.Vector3i;
import com.ylinor.storyteller.data.ActionEnum;
import com.ylinor.storyteller.data.beans.ActionBean;
import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import com.ylinor.itemizer.service.IItemService;

import com.ylinor.storyteller.data.handlers.ConfigurationHandler;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class BookGenerator {
    @Inject
    private Game game;
    @Inject
    private ConfigurationHandler configurationHandler;

    /**
     * Create a bookview from a dialog
     * @param dialog Dialog data
     * @return BookView to display
     */
    public BookView getDialog(DialogBean dialog){
        BookView.Builder bookviewBuilder = BookView.builder();
        for (PageBean pageBean : dialog.getPages()) {
            bookviewBuilder.addPage(generatePage(pageBean));
        }
        return bookviewBuilder.build();
    }

    public BookView getDefaultView(Player player){
        DialogBean dialogBean = new DialogBean(configurationHandler.getIndex());
        PageBean pageBean =new PageBean();
        pageBean.setMessage("Salutations, " + player.getName() + ".");
        dialogBean.getPages().add(pageBean);
        return getDialog(dialogBean);
    }

    /**
     * Generate the text to print inside a page
     * @param page Page data
     * @return Text to display (with optional buttons)
     */
    private Text generatePage(PageBean page){
        Text.Builder text = Text.builder(page.getMessage() + "\n");
        if (!page.getButtonBeanList().isEmpty()) {
            List<ButtonBean> buttons = page.getButtonBeanList();
            for (ButtonBean buttonBean : buttons) {
                text.append(generateButton(buttonBean));
            }
        }
        return text.build();
    }

    public Optional<BookView> getDialog(int dialogid){
        Optional<DialogBean> dialogBeanOptional = configurationHandler.getDialog(dialogid);

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


    /**
     * Generate a button that will commit an action
     * @param buttonBean Button data
     * @return Printed button
     */
    private Text generateButton(ButtonBean buttonBean) {
        Text.Builder textBuilder = Text.builder(buttonBean.getText());
        Optional<TextColor> textColor = game.getRegistry().getType(TextColor.class,buttonBean.getColor().toUpperCase());
        if (textColor.isPresent()) {
            textBuilder.color(textColor.get());
        }
        List<ActionBean> actions = buttonBean.getActions();
        Map<ActionEnum, String> effectiveActions = new HashMap<>();
        for(ActionBean action: actions) {
            effectiveActions.put(ActionEnum.valueOf(action.getName()), action.getArg());
        }
        textBuilder.onClick(TextActions.executeCallback(commandSource-> {
            for (Map.Entry<ActionEnum, String> effectiveAction : effectiveActions.entrySet()) {
                switch (effectiveAction.getKey()) {
                    case OPEN_DIALOG:
                        changeDialog((Player)commandSource,Integer.parseInt(effectiveAction.getValue()));
                        break;
                    case EXECUTE_COMMAND:
                        executeCommand((Player)commandSource, effectiveAction.getValue());
                        break;
                    case TELEPORT:
                        teleport((Player)commandSource, effectiveAction.getValue());
                        break;
                    case GIVE_ITEM:
                        giveItem((Player)commandSource, effectiveAction.getValue());
                        break;
                }
            }
        }));
        return textBuilder.build();
    }

    /**
     * Change the current dialog to a given dialog
     * @param source Player to show dialog to
     * @param dialogIndex Index of the dialog to show
     */
    private void changeDialog(Player source, int dialogIndex) {
        Optional<DialogBean> dialogBeanOptional = configurationHandler.getDialog(dialogIndex);
        if(dialogBeanOptional.isPresent()){
            source.sendBookView(getDialog(dialogBeanOptional.get()));
        } else {
            source.sendMessage(Text.builder("The dialog at the index : "+ dialogIndex + " cannot be loaded.").color(TextColors.RED).build());
        }
    }

    /**
     * Execute a command
     * @param commandText Command line
     */
    private void executeCommand(Player source, String commandText) {
        Sponge.getCommandManager().process(source, commandText);
    }

    /**
     * Teleport the player to a given position
     * @param source Player to teleport
     * @param position Position in a string format
     */
    private void teleport(Player source, String position) {
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
    private void giveItem(Player source, String itemString) {
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

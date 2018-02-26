package com.ylinor.storyteller;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.ylinor.storyteller.data.beans.ButtonBean;
import com.ylinor.storyteller.data.beans.DialogBean;
import com.ylinor.storyteller.data.beans.PageBean;
import com.ylinor.storyteller.data.access.DialogDao;
import com.ylinor.itemizer.service.IItemService;

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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Singleton
public class BookGenerator {
    @Inject
    private Game game;
    @Inject
    private DialogDao dialogDao;

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
        DialogBean dialogBean = new DialogBean(dialogDao.getIndex());
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


    /**
     * Generate a button that will commit an action
     * @param buttonBean Button data
     * @return Printed button
     */
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
            case EXECUTE_COMMAND:
                textBuilder.onClick(TextActions.runCommand(buttonBean.getArg()));
                break;
            case TELEPORT:
                textBuilder.onClick(TextActions.executeCallback(commandSource-> { teleport((Player)commandSource, buttonBean.getArg()); }));
                break;
            case GIVE_ITEM:
                textBuilder.onClick(TextActions.executeCallback(commandSource-> { giveItem((Player)commandSource, buttonBean.getArg()); }));
                break;
        }
        return textBuilder.build();
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
            source.sendMessage(Text.builder("the position : "+ position + " is invalide.").color(TextColors.RED).build());
            e.printStackTrace();
        }
    }

    /**
     * Change the current dialog to a given dialog
     * @param source Player to show dialog to
     * @param dialogIndex Index of the dialog to show
     */
    private void changeDialog(Player source, int dialogIndex) {
        Optional<DialogBean> dialogBeanOptional = dialogDao.getDialog(dialogIndex);
        if(dialogBeanOptional.isPresent()){
            source.sendBookView(getDialog(dialogBeanOptional.get()));
        } else {
            source.sendMessage(Text.builder("The dialog at the index : "+ dialogIndex + " can not be loaded.").color(TextColors.RED).build());
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

package dev.anye.mc.st.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import java.util.function.Consumer;

public class SlotButton extends ResourceHandlerSlot {
    private final Consumer<Player> onClick;

    public SlotButton(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Consumer<Player> onClick) {
        super(handler, slotModifier, index, xPosition, yPosition);
        this.onClick = onClick;
    }

    /*
    public SlotButton(IItemHandler stack, int index, int x, int y, Consumer<Player> onClick) {
        super(stack,index, x, y);
        this.onClick = onClick;
    }

     */

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        if (onClick != null) onClick.accept(player);
        //System.out.println("mayPickup");
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        //if (onClick != null) onClick.accept(player);
    }
}

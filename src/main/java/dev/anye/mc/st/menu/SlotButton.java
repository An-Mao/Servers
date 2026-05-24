package dev.anye.mc.st.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Consumer;

public class SlotButton extends SlotItemHandler {
    private final Consumer<Player> onClick;

    public SlotButton(IItemHandler stack, int index, int x, int y, Consumer<Player> onClick) {
        super(stack,index, x, y);
        this.onClick = onClick;
    }

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

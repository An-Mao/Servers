package dev.anye.mc.st.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class SlotButton<T> extends ResourceHandlerSlot {
	private final Consumer<Player> onClick;
	public final T data;

	public SlotButton(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Consumer<Player> onClick,T data) {
		super(handler, slotModifier, index, xPosition, yPosition);
		this.onClick = onClick;
		this.data = data;
	}

    /*
    public SlotButton(IItemHandler stack, int index, int x, int y, Consumer<Player> onClick) {
        super(stack,index, x, y);
        this.onClick = onClick;
    }

     */

	@Override
	public boolean mayPlace(@NonNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(@NonNull Player player) {
		if (onClick != null) onClick.accept(player);
		return false;
	}

	@Override
	public void onTake(@NonNull Player player, @NonNull ItemStack stack) {
		//if (onClick != null) onClick.accept(player);
	}
}

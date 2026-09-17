package dev.anye.mc.st.menu.button;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.Consumer;

public class SlotDataButton<T> extends SlotButton {
	private final T data;
	public SlotDataButton(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Consumer<Player> onClick, T data) {
		super(handler, slotModifier, index, xPosition, yPosition,onClick);
		this.data = data;
	}

	public T data(){
		return data;
	}
}

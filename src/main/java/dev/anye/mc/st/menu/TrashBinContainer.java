package dev.anye.mc.st.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TrashBinContainer extends PageMenu {
	public static ServerPlayer nowPlayer = null;
	public static final Map<Integer, ItemStack> SLOTS = new HashMap<>();

	public TrashBinContainer(int id, Inventory playerInventory) {
		super(MenuType.GENERIC_9x6, id,playerInventory,getMaxPage(SLOTS.size(),45),45,false,45);
	}
/*
	private void setupSlots(Inventory playerInventory) {
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, row * 9 + col, 8 + col * 18, 18 + row * 18));
			}
		}
		addPageButton();
//		this.addSlot(prevButton(8, 142));
//		this.addSlot(xButton(26, 142));
//		this.addSlot(xButton(44, 142));
//		this.addSlot(xButton(62, 142));
//		this.addSlot(homeButton(80, 142));
//		this.addSlot(xButton(98, 142));
//		this.addSlot(xButton(116, 142));
//		this.addSlot(xButton(134, 142));
//		this.addSlot(nextButton(152, 142));

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}
	}*/

	@Override
	public void addInventory() {
		for (int i = 0; i < pageItemNumber; i++){
			int ii = i;
			this.addItemSlot(i,(x,y) -> new ResourceHandlerSlot(itemHandler, itemHandler::set, ii, x, y));
		}
		/*
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 9; col++) {
				int i = row * 9 + col;
				this.addItemSlot(i,new ResourceHandlerSlot(itemHandler, itemHandler::set, row * 9 + col, 8 + col * 18, 18 + row * 18));
			}
		}

		 */
	}

	@Override
	public void pageRefresh() {
		int num = itemHandler.size();
		int index = pageIndex * 45;
		for (int i = 0; i < num; i++) {
			ItemStack itemStack = SLOTS.getOrDefault(index + i, ItemStack.EMPTY);
			itemHandler.set(i, ItemResource.of(itemStack), itemStack.getCount());
		}
	}
	public void saveItems() {
		int index = pageIndex * 45;
		for (int i = 0; i < 45; i++) {
			//ItemStack stack = itemHandler.getStackInSlot(i);
			//System.out.println(i+ " stack::"+stack);
			SLOTS.put(index + i, itemHandler.getResource(i).toStack(itemHandler.getAmountAsInt(i)));
		}
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int ind) {
		//System.out.println("Inventory slot index:"+ind);
		Slot sourceSlot = slots.get(ind);
		if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();
		if (ind < 45) {
			if (!moveItemStackTo(sourceStack, 54, 90, true)) {
				return ItemStack.EMPTY;
			}
		} else if (ind < 90) {
			if (!moveItemStackTo(sourceStack, 0, 45, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}
		if (sourceStack.getCount() == 0) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}
		sourceSlot.onTake(player, sourceStack);
		return copyOfSourceStack;
	}

	@Override
	public void removed(@NonNull Player player) {
		super.removed(player);
		saveItems();
		nowPlayer = null;
	}

}

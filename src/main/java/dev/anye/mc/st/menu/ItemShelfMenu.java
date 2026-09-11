package dev.anye.mc.st.menu;

import dev.anye.mc.st.sys.Currency;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class ItemShelfMenu extends PageMenu {
	public ItemShelfMenu(int id, Inventory playerInventory) {
		super(MenuType.GENERIC_9x6, id,playerInventory, Currency.I.shelf().getItems().size(),28,true,45);
	}


	@Override
	public void addInventory(){
		int ii = 0;
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 7; col++) {
				int i = 10 + row * 9 + col;
				int iii = ii;
				this.addItemSlot(i,(x, y) -> new SlotButton<>(this.itemHandler,this.itemHandler::set,iii,x,y,null,null));
				ii++;
			}
		}
	}

	@Override
	public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}

	@Override
	public void pageRefresh() {

	}
}

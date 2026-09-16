package dev.anye.mc.st.menu;

import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;

public class AdminMenu extends PageMenu{
	protected AdminMenu(int containerId, Inventory playerInventory, int allItemNumber, int pageItemNumber, boolean border, int pageButtonStartIndex) {
		super(MenuType.GENERIC_9x6, containerId, playerInventory, allItemNumber, pageItemNumber, border, pageButtonStartIndex);
	}

	@Override
	public void addInventory() {

	}

	@Override
	public void pageRefresh() {

	}

	@Override
	public boolean stillValid(Player player) {
		return player.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
	}
}

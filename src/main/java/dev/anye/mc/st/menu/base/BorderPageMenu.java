package dev.anye.mc.st.menu.base;

import net.minecraft.world.entity.player.Inventory;

public abstract class BorderPageMenu extends FooterMenu{
	protected BorderPageMenu(int containerId, Inventory playerInventory, int allItemNumber) {
		super(containerId, playerInventory, allItemNumber, 28);
	}

	@Override
	public void addPageButton() {
		super.addPageButton();
		/*
		0  1  2  3  4  5  6  7  8
		9  10 11 12 13 14 15 16 17
		18 19 20 21 22 23 24 25 26

		27 28 29 30 31 32 33 34 35
		36 37 38 39 40 41 42 43 44
		45 46 47 48 49 50 51 52 53
		 */
		addItemSlot(0, this::xButton);
		addItemSlot(1, this::xButton);
		addItemSlot(2, this::xButton);
		addItemSlot(3, this::xButton);
		addItemSlot(4, this::homeButton);
		addItemSlot(5, this::xButton);
		addItemSlot(6, this::xButton);
		addItemSlot(7, this::xButton);
		addItemSlot(8, this::xButton);

		addItemSlot(9, this::xButton);
		addItemSlot(17, this::xButton);

		addItemSlot(18, this::xButton);
		addItemSlot(26, this::xButton);

		addItemSlot(27, this::xButton);
		addItemSlot(35, this::xButton);

		addItemSlot(36, this::xButton);
		addItemSlot(44, this::xButton);
	}
}

package dev.anye.mc.st.menu.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class FooterMenu extends PageMenu{
	protected FooterMenu(int containerId, Inventory playerInventory, int allItemNumber, int pageItemNumber) {
		super(MenuType.GENERIC_9x6, containerId, playerInventory, allItemNumber, pageItemNumber);
	}
	protected FooterMenu(int containerId, Inventory playerInventory, int allItemNumber) {
		this(containerId, playerInventory, allItemNumber, 45);
	}

	@Override
	public void addPageButton() {
		/*
		0  1  2  3  4  5  6  7  8
		9  10 11 12 13 14 15 16 17
		18 19 20 21 22 23 24 25 26

		27 28 29 30 31 32 33 34 35
		36 37 38 39 40 41 42 43 44
		45 46 47 48 49 50 51 52 53
		 */
		addItemSlot(45,this::xButton);
		addItemSlot(46,this::prevButton);
		addItemSlot(47,this::xButton);

		addItemSlot(48,this::xButton);
		addItemSlot(49,this::firstButton);
		addItemSlot(50,this::xButton);

		addItemSlot(51,this::xButton);
		addItemSlot(52,this::nextButton);
		addItemSlot(53,this::xButton);
	}
}

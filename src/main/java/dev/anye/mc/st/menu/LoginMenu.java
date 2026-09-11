package dev.anye.mc.st.menu;

import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.helper.LoginHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class LoginMenu extends AbstractContainerMenu {
	private final ItemStacksResourceHandler itemHandler;
	private final ServerPlayer serverPlayer;
	private static final int[] inputSlots = {
			12, 13, 14,
			21, 22, 23,
			30, 31, 32,
			39, 40, 41
	};
	/*
	 * 0   1  2  3  4  5  6  7  8
	 * 9  10 11 12 13 14 15 16 17
	 * 18 19 20 21 22 23 24 25 26
	 * 27 28 29 30 31 32 33 34 35
	 * 36 37 38 39 40 41 42 43 44
	 */
	private boolean isInput = false;

	public LoginMenu(int pContainerId, ServerPlayer serverPlayer) {
		super(MenuType.GENERIC_9x6, pContainerId);
		this.serverPlayer = serverPlayer;
		this.itemHandler = new ItemStacksResourceHandler(54);
		int a = 0;
		ItemStack itemStack;
		for (int i : inputSlots) {
			a++;
			itemStack = new ItemStack(Items.ENDER_EYE);
			itemStack.set(DataComponents.CUSTOM_NAME, Component.literal(String.valueOf(a)));
			itemHandler.set(i, ItemResource.of(itemStack), itemStack.getCount());
		}
		itemStack = new ItemStack(Items.BREEZE_ROD);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent(serverPlayer,"login.menu.button.clear"));
		itemHandler.set(inputSlots[9], ItemResource.of(itemStack), itemStack.getCount());

		itemStack = new ItemStack(Items.ENDER_PEARL);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent(serverPlayer,"0"));
		itemHandler.set(inputSlots[10], ItemResource.of(itemStack), itemStack.getCount());

		itemStack = new ItemStack(Items.BLAZE_ROD);
		itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent(serverPlayer,"login.menu.button.login"));
		itemHandler.set(inputSlots[11], ItemResource.of(itemStack), itemStack.getCount());
		addSlots();
	}

	private void addSlots() {
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 9; col++) {
				int slot = row * 9 + col;
				this.addSlot(new SlotButton<>(itemHandler, itemHandler::set, slot, 8 + col * 18, 18 + row * 18, player -> input(itemHandler.getResource(slot).toStack()),null));
			}
		}
	}

	private void input(ItemStack stack) {
		if (isInput || LoginHelper.isLogin(serverPlayer)) return;
		isInput = true;
		if (stack.getItem() == Items.ENDER_EYE || stack.getItem() == Items.ENDER_PEARL) {
			Component name = stack.get(DataComponents.CUSTOM_NAME);
			for (int i = 0; i < 9; i++) {
				if (itemHandler.getResource(i).isEmpty()) {
					ItemStack itemStack = new ItemStack(Items.NETHER_STAR);
					itemStack.set(DataComponents.CUSTOM_NAME, name);
					itemHandler.set(i, ItemResource.of(itemStack), itemStack.getCount());
					break;
				}
			}
			isInput = false;
			return;
		}
		if (stack.getItem() == Items.BREEZE_ROD) {
			for (int i = 8; i >= 0; i--) {
				if (!itemHandler.getResource(i).isEmpty()) {
					itemHandler.set(i, ItemResource.of(ItemStack.EMPTY), 0);
				}
			}
		} else if (stack.getItem() == Items.BLAZE_ROD) {
			StringBuilder p = new StringBuilder();
			boolean f = true;
			for (int i = 0; i < 9; i++) {
				ItemStack itemStack = itemHandler.getResource(i).toStack();
				if (itemStack.getItem() == Items.NETHER_STAR) {
					Component name = itemStack.get(DataComponents.CUSTOM_NAME);
					if (name != null) {
						p.append(name.getString());
					}
				} else {
					f = false;
					break;
				}
			}
			if (f && LoginHelper.Login(serverPlayer, p.toString())) {
				serverPlayer.closeContainer();
			} else {
				error();
			}
		}
		isInput = false;
	}

	public void error() {
		for (int j = 8; j >= 0; j--) {
			itemHandler.set(j, ItemResource.of(new ItemStack(Items.BARRIER)), 1);
		}
	}


	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	@Override
	public void removed(Player pPlayer) {
		if (!LoginHelper.isLogin((ServerPlayer) pPlayer)) {
			serverPlayer.connection.disconnect(Language.getComponent(serverPlayer,"login.failed"));
		}
		super.removed(pPlayer);
	}
}

package dev.anye.mc.st.menu;

import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.PlayerCurrencyData;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.helper.ItemHelper;
import dev.anye.mc.st.menu.base.BorderPageMenu;
import dev.anye.mc.st.menu.button.SlotButton;
import dev.anye.mc.st.sys.currency.Currency;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

public final class STMenu extends BorderPageMenu {
	public STMenu(int containerId, Inventory playerInventory) {
		super(containerId, playerInventory, 4);
	}

	public static void open(ServerPlayer serverPlayer){
		serverPlayer.openMenu(new SimpleMenuProvider(
				(id, playerInventory, player) -> new STMenu(id, playerInventory),
				Language.getComponent(serverPlayer,"trash.menu.title")
		));
	}

	int itemIndex;

	@Override
	protected void pageInitLoad() {
		pageRefresh();
	}

	@Override
	public void addInventory() {
		itemIndex = 0;
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		stack.set(DataComponents.PROFILE,serverPlayer.getProfile());
		stack.set(DataComponents.LORE,new ItemLore(List.of(
				Component.literal("uuid:").append(serverPlayer.getStringUUID()),
				Component.literal(PlayerCurrency.getPlayerCurrency(serverPlayer).read(PlayerCurrencyData::currency,0) + Language.getComponent(serverPlayer,"currency.st.name").getString()).withColor(TextColor.GOLD)
		)));
		this.itemHandler.set(itemIndex, ItemResource.of(stack.copy()),1);
		this.addItemSlot(10,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,itemIndex,x,y, _ -> {}));

/*
		this.addItemSlot(11,this::xButton);
		this.addItemSlot(12,this::xButton);
		this.addItemSlot(13,this::xButton);
		this.addItemSlot(14,this::xButton);
		this.addItemSlot(15,this::xButton);
		this.addItemSlot(16,this::xButton);*/

		itemIndex ++;
		stack = new ItemStack(Items.CHEST);
		stack.set(DataComponents.CUSTOM_NAME,Language.getComponent(serverPlayer,"menu.st.st.trash.name"));
		this.itemHandler.set(itemIndex, ItemResource.of(stack.copy()),1);
		this.addItemSlot(19,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,itemIndex,x,y, _ -> TrashBinContainer.open(serverPlayer)));

		itemIndex ++;
		stack = new ItemStack(Items.EMERALD);
		stack.set(DataComponents.CUSTOM_NAME,Language.getComponent(serverPlayer,"menu.st.st.shelf.item.name"));
		this.itemHandler.set(itemIndex, ItemResource.of(stack.copy()),1);
		this.addItemSlot(20,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,itemIndex,x,y, _ -> ShelfMenu.open(serverPlayer, Currency.I.playerItemShelf())));


		itemIndex ++;
		stack = new ItemStack(Items.EMERALD);
		stack.set(DataComponents.CUSTOM_NAME,Language.getComponent(serverPlayer,"menu.st.st.shelf.system.item.name"));
		this.itemHandler.set(itemIndex, ItemResource.of(stack.copy()),1);
		this.addItemSlot(21,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,itemIndex,x,y, _ -> ShelfMenu.open(serverPlayer,Currency.I.systemItemShelf())));

		itemIndex ++;
		stack = new ItemStack(Items.EMERALD);
		stack.set(DataComponents.CUSTOM_NAME,Language.getComponent(serverPlayer,"menu.st.st.shelf.player.entity.name"));
		this.itemHandler.set(itemIndex, ItemResource.of(stack.copy()),1);
		this.addItemSlot(22,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,itemIndex,x,y, _ -> ShelfMenu.open(serverPlayer,Currency.I.playerEntityShelf())));
/*
		this.addItemSlot(23,this::xButton);
		this.addItemSlot(24,this::xButton);
		this.addItemSlot(25,this::xButton);

		this.addItemSlot(28,this::xButton);
		this.addItemSlot(29,this::xButton);
		this.addItemSlot(30,this::xButton);
		this.addItemSlot(31,this::xButton);
		this.addItemSlot(32,this::xButton);
		this.addItemSlot(33,this::xButton);
		this.addItemSlot(34,this::xButton);

		this.addItemSlot(37,this::xButton);
		this.addItemSlot(38,this::xButton);
		this.addItemSlot(39,this::xButton);
		this.addItemSlot(40,this::xButton);
		this.addItemSlot(41,this::xButton);
		this.addItemSlot(42,this::xButton);*/

		itemIndex ++;

		this.itemHandler.set(itemIndex, ItemHelper.resource(Items.REDSTONE_TORCH,itemStack -> itemStack.set(DataComponents.CUSTOM_NAME,Language.getComponent(serverPlayer,"menu.st.st.admin.name"))),1);
		this.addItemSlot(43,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,itemIndex,x,y, _ -> AdminMenu.open(serverPlayer)));
	}



	@Override
	public void pageRefresh() {}
}

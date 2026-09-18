package dev.anye.mc.st.menu;

import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.command.CommandData;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.helper.ClearHelper;
import dev.anye.mc.st.helper.MsgHelper;
import dev.anye.mc.st.menu.base.FooterMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TrashBinContainer extends FooterMenu {
	private static ServerPlayer nowPlayer;
	public static final Map<Integer, ItemStack> SLOTS = new HashMap<>();

	public TrashBinContainer(int id, Inventory playerInventory) {
		super(id,playerInventory,getMaxPage(SLOTS.size(),45));
	}

	public static void setNowPlayer(ServerPlayer serverPlayer){
		nowPlayer = serverPlayer;
	}
	public static void close(){
		if (nowPlayer != null) nowPlayer.closeContainer();
	}
	public static boolean hasPlayer(){
		if (nowPlayer != null) {
			if (!nowPlayer.hasDisconnected() && nowPlayer.isAlive()) {
				return true;
			}
			nowPlayer.closeContainer();
		}
		return false;
	}


	@Override
	protected void pageInitLoad() {
		pageRefresh();
	}

	@Override
	public void addInventory() {
		for (int i = 0; i < pageItemNumber; i++){
			int ii = i;
			this.addItemSlot(i,(x,y) -> new ResourceHandlerSlot(itemHandler, itemHandler::set, ii, x, y));
		}
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
			SLOTS.put(index + i, itemHandler.getResource(i).toStack(itemHandler.getAmountAsInt(i)));
		}
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int ind) {
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
		setNowPlayer(null);
	}


	public static void open(ServerPlayer serverPlayer) {
		if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::trash))) {
			if (serverPlayer == null) return;
			if (ClearHelper.isClearTrashBin) {
				MsgHelper.sendMsgToPlayerF(serverPlayer,"trash.command.error.cleaning");
				return;
			}
			if (hasPlayer()) {
				MsgHelper.sendMsgToPlayerF(serverPlayer,"trash.command.error.has_player");
				return;
			}
			nowPlayer = serverPlayer;
			serverPlayer.openMenu(new SimpleMenuProvider(
					(id, playerInventory, playerEntity) -> new TrashBinContainer(id, playerInventory),
					Language.getComponent(serverPlayer, "trash.menu.title")
			));
		}
	}
}

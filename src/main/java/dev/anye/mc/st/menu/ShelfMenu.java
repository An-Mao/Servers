package dev.anye.mc.st.menu;

import com.mojang.logging.LogUtils;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.data_type.IShelf;
import dev.anye.mc.st.helper.MsgHelper;
import dev.anye.mc.st.menu.base.BorderPageMenu;
import dev.anye.mc.st.menu.button.SlotButton;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.List;

public class ShelfMenu extends BorderPageMenu {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final IShelf<?> merchandise;
	private List<ItemStack> stacks ;

	public ShelfMenu(int id, Inventory playerInventory, IShelf<?> merchandise) {
		super(id,playerInventory,merchandise.count());
		this.merchandise = merchandise;
		pageRefresh();
	}


	public ItemStack getShelfItem(int index) {
		if (index < 0 || index > stacks.size() - 1) return ItemStack.EMPTY;
		return stacks.get(index);
	}


	@Override
	protected void pageInitLoad() {}

	@Override
	public void addInventory(){
		int ii = 0;
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 7; col++) {
				int i = 10 + row * 9 + col;
				int iii = ii;
				this.addItemSlot(i,(x, y) -> new SlotButton(this.itemHandler,this.itemHandler::set,iii,x,y, player -> this.playerClick(player,iii)));
				ii++;
			}
		}
	}

	public void playerClick(Player player,int index){
		if (player instanceof ServerPlayer player1) {
			ItemStack stack = this.itemHandler.getResource(index).toStack();
			if (stack.isEmpty()){
				return;
			}
			stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(compoundTag -> {

				String uuid = compoundTag.getString(merchandise.idKey()).orElse("");
				if (uuid.isBlank()) return;
				MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.shelf.buy." + (merchandise.buy(player1,uuid) ? "success":"failed"));
			});
		}
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}

	@Override
	public void pageRefresh() {
		stacks = merchandise.all(serverPlayer);

		int pageOffset = this.pageItemNumber * this.pageIndex;
		for (int i = 0; i < itemHandler.size(); i++) {
			ItemStack itemStack = getShelfItem(i + pageOffset);
			if (itemStack.isEmpty()) continue;
			itemHandler.set(i, ItemResource.of(getShelfItem(i + pageOffset)), 1);
		}
	}

	private int getItemIndex(int i){
		int pageOffset = this.pageItemNumber * this.pageIndex;
		return i + pageOffset;
	}

	public static void open(ServerPlayer serverPlayer,IShelf<?> merchandise){
		serverPlayer.openMenu(new SimpleMenuProvider(
				(id, playerInventory, _) -> new ShelfMenu(id, playerInventory,merchandise),
				Language.getComponent(serverPlayer,"menu.st.shelf.item.title")
		));
	}
}

package dev.anye.mc.st.menu;

import com.mojang.logging.LogUtils;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.menu.base.BorderPageMenu;
import dev.anye.mc.st.menu.button.SlotButton;
import dev.anye.mc.st.sys.Currency;
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

public class ItemShelfMenu extends BorderPageMenu {
	private static final Logger LOGGER = LogUtils.getLogger();
	private List<ItemStack> stacks ;
	public ItemShelfMenu(int id, Inventory playerInventory) {
		super( id,playerInventory, Currency.I.shelf().getItems().size());
	}

	public ItemStack getShelfItem(int index) {
		if (index < 0 || index > stacks.size() - 1) return ItemStack.EMPTY;
		return stacks.get(index);
	}


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
			LOGGER.debug("playerClick");
			ItemStack stack = this.itemHandler.getResource(index).toStack();
			if (stack.isEmpty()){
				return;
			}
			stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(compoundTag -> {
				String uuid = compoundTag.getString("shelf.st.item.uuid").orElse("");
				if (uuid.isBlank())return;
				Currency.I.shelf().buyItem(player1,uuid);
			});
		}
	}

	@Override
	public boolean stillValid(@NonNull Player player) {
		return true;
	}

	@Override
	public void pageRefresh() {
		stacks = Currency.I.getShelfItems();

		int pageOffset = this.pageItemNumber * this.pageIndex;
		for (int i = 0; i < itemHandler.size(); i ++){
			ItemStack itemStack = getShelfItem(i + pageOffset);
			if (itemStack.isEmpty()) continue;
			itemHandler.set(i,ItemResource.of(getShelfItem(i + pageOffset)),1);
		}
	}

	private int getItemIndex(int i){
		int pageOffset = this.pageItemNumber * this.pageIndex;
		return i + pageOffset;
	}

	public static void open(ServerPlayer serverPlayer){
		serverPlayer.openMenu(new SimpleMenuProvider(
				(id, playerInventory, _) -> new ItemShelfMenu(id, playerInventory),
				Language.getComponent(serverPlayer,"menu.st.shelf.item.title")
		));
	}
}

package dev.anye.mc.st.menu;

import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.helper.ItemHelper;
import dev.anye.mc.st.menu.base.BorderPageMenu;
import dev.anye.mc.st.menu.button.SlotButton;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class AdminMenu extends BorderPageMenu {
	protected AdminMenu(int containerId, Inventory playerInventory) {
		super(containerId, playerInventory, 0);
	}

	public static boolean hasPermission(ServerPlayer serverPlayer){
		return serverPlayer.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
	}

	public static void open(ServerPlayer serverPlayer){
		serverPlayer.openMenu(new SimpleMenuProvider(
				(id, playerInventory, _) -> new AdminMenu(id, playerInventory),
				Language.getComponent(serverPlayer,"menu.st.admin.title")
		));
	}

	@Override
	protected void pageInitLoad() {

	}

	@Override
	public void addInventory() {
		itemHandler.set(0, ItemHelper.resource(Items.ANVIL,item -> {
			item.set(DataComponents.CUSTOM_NAME,Language.getComponent(serverPlayer,"menu.st.admin.enchantment_extract"));
			item.set(DataComponents.LORE,new ItemLore(List.of(Language.getComponent(serverPlayer, Boolean.TRUE.equals(Config.I.read(configData -> configData.enchantmentExtract)) ? "menu.st.admin.enchantment_extract.state.enable" : "menu.st.admin.enchantment_extract.state.disable"))));
		}),1);
		this.addItemSlot(10,(x,y)->new SlotButton(itemHandler,itemHandler::set,0,x,y,player -> {
			if (player instanceof ServerPlayer player1 && hasPermission(player1)){
				Config.I.update(configData -> configData.enchantmentExtract = !configData.enchantmentExtract);
				Config.I.save();
				AdminMenu.open(serverPlayer);
			}
		}));
	}

	@Override
	public void pageRefresh() {

	}

	@Override
	public boolean stillValid(Player player) {
		if (player instanceof ServerPlayer player1){
			return hasPermission(player1);
		}
		return true;
	}
}

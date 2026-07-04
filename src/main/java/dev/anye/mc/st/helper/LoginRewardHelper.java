package dev.anye.mc.st.helper;

import dev.anye.mc.st.config.login$reward.LoginReward;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoginRewardHelper {
	public static Boolean getRewards(ServerPlayer serverPlayer) {
		if (serverPlayer == null) return false;
		String uuid = serverPlayer.getStringUUID();
		List<String> list = new ArrayList<>();
		if (LoginReward.loginData.getDatas().containsKey(LoginReward.getDayCheck())) {
			list = LoginReward.loginData.getDatas().get(LoginReward.getDayCheck());
			if (list.contains(uuid)) {
				return false;
			}
		}

		list.add(uuid);
		LoginReward.loginData.getDatas().put(LoginReward.getDayCheck(), list);
		LoginReward.loginData.save();
		List<ItemStack> rewards = new ArrayList<>(LoginReward.I.getDayReward(uuid));
		if (rewards.isEmpty()) return false;
		SimpleContainer container = new SimpleContainer(9);
		int i = 0;
		for (ItemStack itemStack : rewards) {
			container.setItem(i, itemStack);
			i++;
		}
		SimpleMenuProvider menuProvider = new SimpleMenuProvider(
				(id, playerInventory, playerEntity) -> new ChestMenu(MenuType.GENERIC_9x1, id, playerInventory, container, 1),
				Component.literal("Custom Chest with Items")
		);
		serverPlayer.openMenu(menuProvider);
		return true;
	}
}

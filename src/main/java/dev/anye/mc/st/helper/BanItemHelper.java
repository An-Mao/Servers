package dev.anye.mc.st.helper;

import dev.anye.mc.st.config.ban_item.BanItemConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BanItemHelper {
	public static boolean checkItem(ItemStack itemStack) {
		return BanItemConfig.I.isBanned(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
	}

	public static boolean checkItem(ItemLike itemStack) {
		return BanItemConfig.I.isBanned(BuiltInRegistries.ITEM.getKey(itemStack.asItem()).toString());
	}

	public static boolean checkItemAndSend(Entity entity) {
		if (BanItemConfig.I.getData().enable() && entity instanceof ItemEntity itemEntity) {
			ItemStack itemStack = itemEntity.getItem();
			if (BanItemHelper.checkItem(itemStack)) {
				MsgHelper.sendServerMsg(itemEntity.level().getServer(), BanItemConfig.I.getData().msg());
				return true;
			}
		}
		return false;
	}

	public static boolean checkItemAndSend(ItemLike itemStack, MinecraftServer server) {
		if (BanItemConfig.I.getData().enable()) {
			if (BanItemHelper.checkItem(itemStack)) {
				MsgHelper.sendServerMsg(server, BanItemConfig.I.getData().msg());
				return true;
			}
		}
		return false;
	}

	public static boolean checkItemAndSend(ItemStack itemStack, MinecraftServer server) {
		if (BanItemConfig.I.getData().enable()) {
			if (BanItemHelper.checkItem(itemStack)) {
				MsgHelper.sendServerMsg(server, BanItemConfig.I.getData().msg());
				return true;
			}
		}
		return false;
	}
}

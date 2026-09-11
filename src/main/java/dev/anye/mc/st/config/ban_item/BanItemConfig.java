package dev.anye.mc.st.config.ban_item;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.ItemHelper;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class BanItemConfig extends _JsonConfigS<BanItemData> {
	private static final String FILE = _File.getFilePath(ConfigDir.BLACK_LIST, "BanItem.json");
	public static final BanItemConfig I = new BanItemConfig();

	public BanItemConfig() {
		super(FILE, BanItemData.DEFAULT, new TypeToken<>() {});
	}

	public List<String> getItems() {
		return read(BanItemData::bannedItems,new ArrayList<>());
	}

	public boolean checkItem(ItemStack itemStack) {
		return checkItem(itemStack.getItem());
	}

	public boolean checkItem(ItemLike itemStack) {
		return isBanned(BuiltInRegistries.ITEM.getKey(itemStack.asItem()).toString());
	}

	public boolean isBanned(String item) {
		return read(banItemData -> {
			if (banItemData.enable()){
				return banItemData.bannedItems().contains(item);
			}
			return false;
		},false);
	}



	public boolean checkItemAndSend(ItemEntity entity) {
		return checkItemAndSend(entity.getItem(),entity.level().getServer());
	}

	public boolean checkItemAndSend(Item itemStack, MinecraftServer server) {
		return read(banItemData -> {
			if (banItemData.enable() && banItemData.bannedItems().contains(ItemHelper.getStringKey(itemStack))) {
				MsgHelper.sendServerMsg(server, banItemData.msg());
				return true;
			}
			return false;
		}, false);
	}

	public boolean checkItemAndSend(ItemStack itemStack, MinecraftServer server) {
		return checkItemAndSend(itemStack.getItem(),server);
	}
}

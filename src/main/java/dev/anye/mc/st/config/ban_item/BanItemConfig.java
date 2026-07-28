package dev.anye.mc.st.config.ban_item;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.ArrayList;
import java.util.List;

public class BanItemConfig extends _JsonConfig<BanItemData> {
	private static final String FILE = _File.getFilePath(ConfigDir.BLACK_LIST, "BanItem.json");
	public static final BanItemConfig I = new BanItemConfig();

	public BanItemConfig() {
		super(FILE, BanItemData.DEFAULT, new TypeToken<>() {});
	}

	public List<String> getItems() {
		if (this.data.isPresent()){
			return data.get().bannedItems();
		}
		return new ArrayList<>();
	}

	public boolean isBanned(String item) {
		return getItems().contains(item);
	}
}

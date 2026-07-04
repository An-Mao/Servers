package dev.anye.mc.st.config.ban$item;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.ArrayList;
import java.util.List;

public class BanItemConfig extends _JsonConfig<BanItemData> {
	private static final String file = _File.getFilePath(ConfigDir.BLACK_LIST, "BanItem.json");
	public static final BanItemConfig I = new BanItemConfig();

	public BanItemConfig() {
		super(file, """
				{
				    "enable": true,
				    "msg": "ban.item.deny",
				    "bannedItems": [
				        "minecraft:bedrock"
				    ]
				}
				""", new TypeToken<>() {
		});
	}

	public List<String> getItems() {
		//System.out.println(getDatas().bannedItems);
		if (getDatas().bannedItems == null) getDatas().bannedItems = new ArrayList<>();
		return getDatas().bannedItems;
	}

	public boolean isBanned(String item) {
		//System.out.println(getItems());
		return getItems().contains(item);
	}
}

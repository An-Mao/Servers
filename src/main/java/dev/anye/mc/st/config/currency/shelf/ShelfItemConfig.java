package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

/**
 * 货架物品配置类
 */
public class ShelfItemConfig extends _JsonConfigR<ShelfItemConfigData> {
	public static final String FILE = _File.getFilePath(ConfigDir.BASE,"ItemShelf.json");

	public ShelfItemConfig() {
		super(FILE, ShelfItemConfigData.DEFAULT, new TypeToken<>(){});
	}
}

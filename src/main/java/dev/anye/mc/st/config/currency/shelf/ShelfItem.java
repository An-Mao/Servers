package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.UUID;

/**
 * 货架物品类
 */
public class ShelfItem extends _JsonConfigR<ShelfItemData> {
	public ShelfItem(UUID uuid, ShelfItemData newData) {
		this(uuid.toString(),newData);
	}
	public ShelfItem(String uuid, ShelfItemData newData) {
		super(_File.getFilePath(ConfigDir.CURRENCY_SHELF_ITEM,uuid+ _SuffixCDT.JSON_SUFFIX), newData, new TypeToken<>(){}, true);
	}
}

package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

/**
 * 用来记录系统商店物品售出记录的基础类
 */
public class SystemShelfItemLog extends _JsonConfigS<SystemShelfItemLogData> {
	public SystemShelfItemLog(String key) {
		super(_File.getFilePath(ConfigDir.CURRENCY_SHELF_SYSTEM_ITEM_LOG,key + _SuffixCDT.JSON_SUFFIX), SystemShelfItemLogData.EMPTY, new TypeToken<>(){}, false);
	}
}

package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

/**
 * 用来加载系统商店物品信息的基础类
 */
public class SystemShelfItemIO extends _JsonConfigR<SystemShelfItemData> {
	public SystemShelfItemIO(String key) {
		super(_File.getFilePath(ConfigDir.SHELF_SYSTEM_ITEM,key + _SuffixCDT.JSON_SUFFIX), null, new TypeToken<>(){}, false);
	}
}

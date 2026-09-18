package dev.anye.mc.st.config.currency.shelf.entity;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

public class ShelfEntity extends _JsonConfigR<ShelfEntityData> {
	public ShelfEntity(String key, ShelfEntityData defaultRawData) {
		super(_File.getFilePath(ConfigDir.SHELF_ENTITY,key+ _SuffixCDT.JSON_SUFFIX), defaultRawData, new TypeToken<>(){}, true);
	}
}

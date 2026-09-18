package dev.anye.mc.st.config.currency.shelf.entity;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

public class ShelfEntityConfig extends _JsonConfigR<ShelfEntityConfigData> {
	public static final String FILE = _File.getFilePath(ConfigDir.BASE,"EntityShelf.json");
	public ShelfEntityConfig() {
		super(FILE, ShelfEntityConfigData.DEFAULT, new TypeToken<>(){});
	}
}

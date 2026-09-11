package dev.anye.mc.st.config;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;

public class Config extends _JsonConfigS<ConfigData> {
	private static final String FILE = _File.getFilePath(ConfigDir.BASE, "config.json");
	public static final Config I = new Config();

	public Config() {
		super(FILE, ConfigData.Default(), new TypeToken<>() {});
	}
}

package dev.anye.mc.st.config.command;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

public class CommandConfig extends _JsonConfig<CommandData> {
	public static final String FILE_PATH = _File.getFilePath(ConfigDir.BASE, "Commands.json");
	public static final CommandConfig I = new CommandConfig();

	public CommandConfig() {
		super(FILE_PATH, CommandData.DEFAULT, new TypeToken<>() {});
	}
}

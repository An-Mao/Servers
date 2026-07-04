package dev.anye.mc.st.config.command;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

public class CommandConfig extends _JsonConfig<CommandData> {
	public static final String filePath = _File.getFilePath(ConfigDir.BASE, "Commands.json");
	public static final CommandConfig I = new CommandConfig();

	public CommandConfig() {
		super(filePath, """
				{
				    "commandRoot": "st",
				    "home": true,
				    "setHome": true,
				    "back": true,
				    "backMaxCount": 5,
				    "tpa": true,
				    "tpaAccept": true,
				    "tpaDeny": true,
				    "reward": true,
				    "trash": true
				}
				""", new TypeToken<>() {
		});
	}
}

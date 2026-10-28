package dev.anye.mc.st.config.player$group;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerGroupConfig {
	public static final Map<String, List<String>> GROUPS = getGroups();
	private PlayerGroupConfig(){}
	public static Map<String, List<String>> getGroups() {
		Map<String, List<String>> abc = new HashMap<>();
		List<Path> jsonFiles = _File.getFiles(ConfigDir.PLAYER_GROUP, ".json");
		for (Path path : jsonFiles) {
			String fileName = path.getFileName().toString();
			new Group(path.toString()).read(strings -> {
				abc.put(fileName,strings);
			});

		}
		return abc;
	}

	public static class Group extends _JsonConfigS<List<String>> {
		public Group(String filePath) {
			super(filePath, new ArrayList<>(), new TypeToken<>() {});
		}


		@Override
		public String toString() {
			return data.toString();
		}
	}
}

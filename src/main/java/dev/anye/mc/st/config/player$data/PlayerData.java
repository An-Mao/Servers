package dev.anye.mc.st.config.player$data;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.data$type.PosData;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class PlayerData extends _JsonConfig<PD> {
	public static final HashMap<String, PlayerData> I = loadPlayerData();

	public PlayerData(String filePath) {
		super(filePath, """
				""", new TypeToken<>() {
		});
	}

	@Override
	public PD getDatas() {
		if (datas == null) datas = new PD();
		return datas;
	}


	public static HashMap<String, PlayerData> loadPlayerData() {
		HashMap<String, PlayerData> abc = new HashMap<>();
		List<Path> jsonFiles = _File.getFiles(ConfigDir.PLAYER_DATA, ".json");
		for (Path path : jsonFiles) {
			String fileName = path.getFileName().toString();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			abc.put(fileName, new PlayerData(path.toString()));
		}
		return abc;

	}


	public static PlayerData get(String uuid) {
		return I.getOrDefault(uuid, new PlayerData(_File.getFilePath(ConfigDir.PLAYER_DATA, uuid + ".json")));
	}


	public void pullBack(PosData back) {
	}

	public @Nullable PosData getBack() {
		List<PosData> backs = getDatas().backs;
		if (backs == null || backs.isEmpty()) return null;
		PosData pd = backs.getLast().copy();
		backs.removeLast();
		getDatas().backs = backs;
		save();
		return pd;
	}

	public void addBack(ServerPlayer serverPlayer) {
		addBack(PosData.create(serverPlayer));
	}

	public void addBack(PosData pd) {
		getDatas().addBack(pd);
		save();
	}

	public void setHome(PosData posData) {
		getDatas().home = posData;
		save();
	}
}

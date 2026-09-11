package dev.anye.mc.st.config.player_data;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.data_type.PosData;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerConfig extends _JsonConfigS<PlayerData> {
	//public static final Map<String, PlayerConfig> I = loadPlayerData();
	public PlayerConfig(String filePath) {
		super(filePath, new PlayerData(), new TypeToken<>() {});
	}


	public static Map<String, PlayerConfig> loadPlayerData() {
		HashMap<String, PlayerConfig> abc = new HashMap<>();
		List<Path> jsonFiles = _File.getFiles(ConfigDir.PLAYER_DATA, _SuffixCDT.JSON_SUFFIX);
		for (Path path : jsonFiles) {
			String fileName = path.getFileName().toString();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			abc.put(fileName, new PlayerConfig(path.toString()));
		}
		return abc;

	}

	public static PlayerConfig get(ServerPlayer player) {
		return get(player.getStringUUID());
	}

	public static PlayerConfig get(String uuid) {
		return new PlayerConfig(_File.getFilePath(ConfigDir.PLAYER_DATA, uuid + _SuffixCDT.JSON_SUFFIX));
		//return I.getOrDefault(playerUUID, new PlayerConfig(_File.getFilePath(ConfigDir.PLAYER_DATA, playerUUID + ".json")));
	}


	public void sendMessage(ServerPlayer serverPlayer,String raw,Object... value){
		sendMessage(serverPlayer,raw,raw,value);
	}
	public void sendMessage(ServerPlayer serverPlayer,String raw,String def,Object... value){
		read(playerData ->{
			MsgHelper.sendMsgToPlayer(serverPlayer, Language.translatable(playerData.lang(),raw,def,value));
		});
	}

	public void sendFormatMessage(ServerPlayer serverPlayer,String raw,Map<String,String> format,Object... value){
		sendMessage(serverPlayer,raw,raw,format,value);
	}
	public void sendFormatMessage(ServerPlayer serverPlayer,String raw,String def,Map<String,String> format,Object... value){
		read(playerData -> {
			AtomicReference<String> msg = new AtomicReference<>(Language.translatable(playerData.lang(), raw, def, value));
			format.forEach((s, s2) -> msg.set(msg.get().replace(s, s2)));
			MsgHelper.sendMsgToPlayer(serverPlayer, msg.get());
		});
	}

	public String lang(){
		return read(PlayerData::lang,"en_us");
	}


	public void setLang(String lang){
		update(playerData -> playerData.setLang(lang));
		save();
	}

	public void pullBack(PosData back) {
	}

	public @Nullable PosData getBack() {
		AtomicReference<PosData> pd = new AtomicReference<>(null);
		update(playerData -> {
			List<PosData> backs = playerData.backs;
			if (backs == null || backs.isEmpty()) return;
			pd.set(backs.getLast().copy());
			backs.removeLast();
			playerData.backs = backs;
		});
		save();
		return pd.get();
	}

	public void addBack(ServerPlayer serverPlayer) {
		addBack(PosData.create(serverPlayer));
	}

	public void addBack(PosData pd) {
		update(playerData -> playerData.addBack(pd));
		save();
	}

	public void setHome(PosData posData) {
		update(playerData -> playerData.home = posData);
		save();
	}
}

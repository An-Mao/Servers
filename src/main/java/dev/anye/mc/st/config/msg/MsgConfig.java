package dev.anye.mc.st.config.msg;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsgConfig {
	private static final String FIRST_JOIN_FILE = _File.getFilePath(ConfigDir.MSG, "FirstJoin.json");
	private static final String EVERY_DAY_JOIN_FILE = _File.getFilePath(ConfigDir.MSG, "EveryDayJoin.json");
	private static final String EVERY_JOIN_FILE = _File.getFilePath(ConfigDir.MSG, "EveryJoin.json");
	public static final FirstJoin FIRST_JOIN = new FirstJoin();
	public static final EveryDayJoin EVERY_DAY_JOIN = new EveryDayJoin();
	public static final EveryJoin EVERY_JOIN = new EveryJoin();
	private MsgConfig(){}
	public static class FirstJoin extends _JsonConfigS<MsgConfigData> {
		public final FirstLog firstLog = new FirstLog();
		public FirstJoin() {
			super(FIRST_JOIN_FILE, MsgConfigData.FIRST_JOIN_DEFAULT, new TypeToken<>() {});
		}

		public void send(ServerPlayer serverPlayer) {
			read(msgConfigData -> {
				if (msgConfigData.enable()) {
					firstLog.update(log ->{
						if (log.contains(serverPlayer.getStringUUID())) return;
						MsgHelper.sendMsgToPlayerF(serverPlayer, msgConfigData.msg());
						log.add(serverPlayer.getStringUUID());
					});
					firstLog.saveIfDirtyAsync();
				}
			});
		}
	}

	public static class EveryDayJoin extends _JsonConfigS<MsgConfigData> {
		private static String today = getDay();
		public final DayLog dayLog = new DayLog();

		public EveryDayJoin() {
			super(EVERY_DAY_JOIN_FILE, MsgConfigData.EVERY_DAY_JOIN_DEFAULT, new TypeToken<>() {});
		}

		public void send(ServerPlayer serverPlayer) {
			read(msgConfigData -> {
				if (msgConfigData.enable()){
					dayLog.update(stringListHashMap -> {
						List<String> list = stringListHashMap.getOrDefault(today, new ArrayList<>());

						if (today.equals(getDay())) {
							if (!stringListHashMap.getOrDefault(today, new ArrayList<>()).contains(serverPlayer.getStringUUID())) {
								MsgHelper.sendMsgToPlayerF(serverPlayer, msgConfigData.msg());
							} else {
								return;
							}
						} else {
							MsgHelper.sendMsgToPlayerF(serverPlayer, msgConfigData.msg());
							today = getDay();
							stringListHashMap.clear();
						}
						list.add(serverPlayer.getStringUUID());
						stringListHashMap.put(today, list);
					});
					dayLog.save();
				}
			});
		}

		public static String getDay() {
			return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
		}
	}

	public static class EveryJoin extends _JsonConfigS<MsgConfigData> {
		public EveryJoin() {
			super(EVERY_JOIN_FILE, MsgConfigData.EVERY_JOIN_DEFAULT, new TypeToken<>() {
			});
		}

		public void send(ServerPlayer serverPlayer) {
			read(msgConfigData -> {
				if (msgConfigData.enable()) MsgHelper.sendMsgToPlayerF(serverPlayer, msgConfigData.msg());
			});
		}
	}


	public static class FirstLog extends _JsonConfigS<List<String>> {
		private static final String FILE = _File.getFilePath(ConfigDir.MSG, "FirstLog.json");
		public FirstLog() {
			super(FILE, new ArrayList<>(), new TypeToken<>(){});
		}
	}

	public static class DayLog extends _JsonConfigS<Map<String, List<String>>> {
		private static final String FILE = _File.getFilePath(ConfigDir.MSG, "DayLog.json");

		public DayLog() {
			super(FILE, new HashMap<>(), new TypeToken<>() {});
		}

	}
}

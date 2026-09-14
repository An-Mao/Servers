package dev.anye.mc.st.helper;

import com.mojang.logging.LogUtils;
import dev.anye.core.format._FormatToString;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import dev.anye.mc.st.js.CJS;
import dev.anye.mc.st.js._JavaScript;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class MsgHelper {
	private static final Logger LOGGER = LogUtils.getLogger();
	private MsgHelper(){}
	/**
	 * 创建一个简易的map
	 * @param a 待替换的内容
	 * @param b 用来替换的内容
	 * @return map
	 */
	public static Map<String, String> createMsgMap(String a, String b) {
		HashMap<String, String> map = new HashMap<>();
		map.put(a, b);
		return map;
	}

	/**
	 * 向所有玩家发送消息，消息为空或server为null则不会发送任何消息
	 * @param server MinecraftServer
	 * @param msg String
	 */
	public static void sendServerMsg(MinecraftServer server, String msg) {
		if (msg.isEmpty() || server == null) return;

		server.getPlayerList().getPlayers().forEach(serverPlayer -> PlayerConfig.get(serverPlayer).sendMessage(serverPlayer,msg,msg));
	}

	/**
	 * 向所有玩家发送消息，消息为空或server为null则不会发送任何消息
	 * @param server server
	 * @param msg msg
	 * @param map 要替换的内容
	 */
	public static void sendServerMsg(MinecraftServer server, String msg, Map<String, String> map) {
		if (msg.isEmpty() || server == null) return;
		server.getPlayerList().getPlayers().forEach(serverPlayer -> PlayerConfig.get(serverPlayer).sendFormatMessage(serverPlayer,msg,msg,map));
	}

	/**
	 * 发送消息到level内的所有玩家，消息为空或serverLevel为null则不会发送任何消息
	 * @param serverLevel level
	 * @param msg msg
	 */
	public static void sendServerMsg(ServerLevel serverLevel,String msg) {
		if (msg.isBlank() || serverLevel == null) return;
		sendServerMsg(serverLevel.getServer(), msg);
	}


	/**
	 * 向指定玩家发送本地化的消息内容
	 * @param serverPlayer 接收消息的玩家
	 * @param msg 消息，通常为key，后续会获取本地化翻译
	 */
	public static void sendMsgToPlayerF(ServerPlayer serverPlayer, String msg) {
		if (msg.isEmpty()) return;
		PlayerConfig.get(serverPlayer).sendMessage(serverPlayer,msg);
	}

	/**
	 * 向指定玩家发送本地化的消息内容
	 * @param serverPlayer 接收消息的玩家
	 * @param msg 消息，通常为key，后续会获取本地化翻译
	 * @param map 替换的内容
	 */
	public static void sendMsgToPlayerF(ServerPlayer serverPlayer, String msg, Map<String, String> map) {
		if (msg.isEmpty()) return;
		PlayerConfig.get(serverPlayer).sendFormatMessage(serverPlayer,msg,map);
	}

	/**
	 * 向指定玩家发送本地化的消息内容
	 * @param serverPlayer 接收消息的玩家
	 * @param msg 消息，通常为key，后续会获取本地化翻译
	 * @param map 替换的内容
	 */
	public static void sendMsgToPlayerF(ServerPlayer serverPlayer, String msg, Map<String, String> map,Object... v) {
		if (msg.isEmpty()) return;
		PlayerConfig.get(serverPlayer).sendFormatMessage(serverPlayer,msg,map,v);
	}

	/**
	 * 向指定玩家发送消息，消息不会进行本地化翻译，但依旧会进行格式化{@link MsgHelper#format}
	 * @param serverPlayer 接收消息的玩家
	 * @param msg 消息
	 */
	public static void sendMsgToPlayer(ServerPlayer serverPlayer, String msg) {
		if (msg.isEmpty()) return;
		msg = MsgHelper.format(serverPlayer, msg, new HashMap<>());
		if (msg.isEmpty()) return;
		serverPlayer.sendSystemMessage(Component.literal(msg));
	}

	/**
	 * 格式化消息，使用map中的值替换消息中对应的键
	 * @param player 接收消息的玩家
	 * @param msg 消息
	 * @param map 替换内容
	 * @return 格式化后的内容
	 */
	private static String format(ServerPlayer player, String msg, Map<String, Object> map) {
		if (msg.toLowerCase().endsWith(".js")) {
			_JavaScript<?> easyJS = CJS.GetNewInstance();
			easyJS.addParameter("player", player);
			map.forEach(easyJS::addParameter);
			Object result = easyJS.runFile(_File.getFilePath(ConfigDir.JAVASCRIPT, msg));
			if (result != null) {
				return result.toString();
			}
			// "error js: "+msg;
			return "";
		} else {
			if (!map.isEmpty()) {
				String[] m = {msg};
				map.forEach((s, o) -> m[0] = m[0].replace(s,o.toString()));
				msg = m[0];
			}
			msg = msg.replace("$player.name", player.getName().getString())
					.replace("$player.health", _FormatToString.formatValue(player.getHealth(), 2))
					.replace("$player.maxHealth", _FormatToString.formatValue(player.getMaxHealth(), 2))
					.replace("$player.expLevel", _FormatToString.formatValue(player.experienceLevel, 2));
			return msg;
		}
	}


	/**
	 * 向指定玩家发送消息，消息不会进行本地化翻译，但依旧会进行格式化{@link MsgHelper#format}
	 * @param serverPlayer 接收消息的玩家
	 * @param msg 消息
	 * @param map 替换的内容
	 */
	public static void sendWithArgs(ServerPlayer serverPlayer, String msg, Map<String, Object> map) {
		if (msg.isEmpty()) return;
		msg = MsgHelper.format(serverPlayer, msg, map);
		if (msg.isEmpty()) return;
		serverPlayer.sendSystemMessage(Component.literal(msg));
	}

	/**
	 * 向所有玩家发送消息，消息不会进行本地化翻译，但依旧会进行格式化{@link MsgHelper#format}
	 * @param server server
	 * @param msg 消息
	 * @param map 替换的内容
	 */
	public static void sendWithArgs(MinecraftServer server, String msg, Map<String, Object> map) {
		if (msg.isEmpty()) return;
		server.getPlayerList().getPlayers().forEach(serverPlayer -> MsgHelper.sendWithArgs(serverPlayer, msg, map));
	}
}

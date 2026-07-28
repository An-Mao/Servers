package dev.anye.mc.st.helper;

import dev.anye.mc.st.config.player$group.PlayerGroupConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class PlayerHelper {
	public static boolean checkPlayer(ServerPlayer player, String... s) {
		return checkPlayer(player, List.of(s));
	}

	public static boolean checkPlayer(ServerPlayer player, List<String> list) {
		boolean[] has = {false};
		list.forEach(s -> {
			if (has[0]) return;
			if (s.equals(player.getStringUUID())) {
				has[0] = true;
				return;
			}
			if (s.equals(player.getName().getString())) {
				has[0] = true;
				return;
			}
			if (PlayerGroupConfig.GROUPS.containsKey(s)) {
				has[0] = checkPlayer(player, PlayerGroupConfig.GROUPS.get(s).getData());
			}
		});
		return has[0];
	}

	public static boolean checkPlayer(ServerPlayer player, String s) {
		if (s.equals(player.getStringUUID())) return true;
		if (s.equals(player.getName().getString())) return true;
		if (PlayerGroupConfig.GROUPS.containsKey(s)) {
			boolean[] has = {false};
			PlayerGroupConfig.GROUPS.get(s).getData().forEach(s1 -> {
				has[0] = checkPlayer(player, s1);
			});
			return has[0];
		}
		return false;
	}
}

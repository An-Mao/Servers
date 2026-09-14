package dev.anye.mc.st.helper;

import dev.anye.core.bytes._Byte;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.config.login.LoginConfig;
import dev.anye.mc.st.config.login.LoginData;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import dev.anye.mc.st.menu.LoginMenu;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class LoginHelper {
	private static final List<String> players = new ArrayList<>();

	public static boolean checkLogin(ServerPlayer serverPlayer) {
		if (Boolean.TRUE.equals(LoginConfig.INSTANCE.read(LoginData::enable))) {
			return !LoginHelper.isLogin(serverPlayer);
		}
		return false;
	}


	public static void openLogin(ServerPlayer serverPlayer) {
		if (!serverPlayer.isAlive()) {
			serverPlayer.setHealth(serverPlayer.getMaxHealth());
			MinecraftServer server = serverPlayer.level().getServer();
			server.getPlayerList().respawn(serverPlayer, false, Entity.RemovalReason.KILLED);
		}
		serverPlayer.openMenu(new SimpleMenuProvider(
				(id, playerInventory, playerEntity) -> new LoginMenu(id, serverPlayer),
				Language.getComponent(serverPlayer,"login.menu.title")
		));
	}


	public static boolean isLogin(ServerPlayer player) {
		return players.contains(player.getStringUUID());
	}

	public static void addLogin(ServerPlayer player) {
		players.add(player.getStringUUID());
	}

	public static void removeLogin(ServerPlayer player) {
		players.remove(player.getStringUUID());
	}

	public static boolean Login(ServerPlayer player, String password) {
		if (isLogin(player)) {
			MsgHelper.sendMsgToPlayerF(player, LoginConfig.INSTANCE.read(LoginData::fail,""));
			return false;
		}
		boolean[] l = {false};
		PlayerConfig playerConfig = PlayerConfig.get(player);
		if (Boolean.TRUE.equals(playerConfig.read(playerData -> {
			if (playerData.emptyPassword()){
				l[0] = true;
				return true;
			}
			return playerData.checkPassword(player,password);
		}))){
			if (l[0]){
				playerConfig.update(playerData -> playerData.setPassword(player,password));
				playerConfig.save();
			}
			addLogin(player);
			MsgHelper.sendMsgToPlayerF(player, LoginConfig.INSTANCE.read(LoginData::success,""));
			return true;
		}else {
			MsgHelper.sendMsgToPlayerF(player, LoginConfig.INSTANCE.read(LoginData::fail,""));
			return false;
		}
	}
}

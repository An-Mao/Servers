package dev.anye.mc.st.helper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.config.ban_item.BanItemConfig;
import dev.anye.mc.st.config.black_list.BlackListConfig;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.login_reward.LoginReward;
import dev.anye.mc.st.config.msg.MsgConfig;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import dev.anye.mc.st.config.player$group.PlayerGroupConfig;
import dev.anye.mc.st.data_type.PosData;
import dev.anye.mc.st.menu.LoginMenu;
import dev.anye.mc.st.menu.TrashBinContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Relative;

import java.util.HashMap;
import java.util.UUID;

public class CommandHelper {
	private static final int FAILED = 0;
	private static final int SUCCESS = Command.SINGLE_SUCCESS;

	public static int trash(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getData().trash()) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player == null) return FAILED;
			if (ClearHelper.isClearTrashBin) {
				sendSuccess(context,"trash.command.error.cleaning");
				return SUCCESS;
			}
			if (TrashBinContainer.nowPlayer != null) {
				sendSuccess(context,"trash.command.error.has_player");
				return SUCCESS;
			}
			TrashBinContainer.nowPlayer = player;
			player.openMenu(new SimpleMenuProvider(
					(id, playerInventory, playerEntity) -> new TrashBinContainer(id, playerInventory),
					Language.getComponent(player,"trash.menu.title")
			));
			return SUCCESS;
		}
		return FAILED;
	}

	public static int login(CommandContext<CommandSourceStack> context) {
		ServerPlayer player = context.getSource().getPlayer();
		if (player != null) {
			player.openMenu(new SimpleMenuProvider(
					(id, playerInventory, playerEntity) -> new LoginMenu(id, player),
					Language.getComponent(player,"password.menu.title")
			));
			return SUCCESS;
		}
		return FAILED;
	}

	public static int clearAll(CommandContext<CommandSourceStack> context) {
		ClearHelper.clearServerEntity(context.getSource().getServer());
		ClearHelper.clearItem(context.getSource().getServer());
		return SUCCESS;
	}

	public static int clearEntity(CommandContext<CommandSourceStack> context) {
		ClearHelper.clearServerEntity(context.getSource().getServer());
		return SUCCESS;
	}

	public static int clearItem(CommandContext<CommandSourceStack> context) {
		ClearHelper.clearItem(context.getSource().getServer());
		return SUCCESS;
	}

	public static int clearTrash(CommandContext<CommandSourceStack> context) {
		TrashBinContainer.SLOTS.clear();
		return SUCCESS;
	}

	public static int reload(CommandContext<CommandSourceStack> context) {
		Config.I.init();
		PlayerGroupConfig.GROUPS.clear();
		PlayerGroupConfig.GROUPS.putAll(PlayerGroupConfig.getGroups());
		ClearConfig.ENTITY_CLEAR.init();
		ClearConfig.ITEM_CLEAR.init();
		MsgConfig.FIRST_JOIN.init();
		MsgConfig.EVERY_DAY_JOIN.init();
		MsgConfig.EVERY_JOIN.init();
		Language.reloadLanguage();
		BlackListConfig.INSTANCE.init();
		BanItemConfig.I.init();
		LoginReward.I.init();
		sendSuccess(context,"reload.success.all");
		return SUCCESS;
	}

	public static int reloadConfig(CommandContext<CommandSourceStack> context) {
		Config.I.init();
		sendSuccess(context,"reload.success.config");
		return SUCCESS;
	}

	public static int reloadPlayerGroup(CommandContext<CommandSourceStack> context) {
		PlayerGroupConfig.GROUPS.clear();
		PlayerGroupConfig.GROUPS.putAll(PlayerGroupConfig.getGroups());
		sendSuccess(context,"reload.success.player_group");
		return SUCCESS;
	}

	public static int reloadClear(CommandContext<CommandSourceStack> context) {
		ClearConfig.ENTITY_CLEAR.init();
		ClearConfig.ITEM_CLEAR.init();
		sendSuccess(context,"reload.success.clear");
		return SUCCESS;
	}

	public static int reloadMsg(CommandContext<CommandSourceStack> context) {
		MsgConfig.FIRST_JOIN.init();
		MsgConfig.EVERY_DAY_JOIN.init();
		MsgConfig.EVERY_JOIN.init();
		sendSuccess(context,"reload.success.msg");
		return SUCCESS;
	}

	public static int reloadLanguage(CommandContext<CommandSourceStack> context) {
		Language.reloadLanguage();
		sendSuccess(context,"reload.success.language");
		return SUCCESS;
	}

	public static int reloadBlackList(CommandContext<CommandSourceStack> context) {
		BlackListConfig.INSTANCE.init();
		sendSuccess(context,"reload.success.black_list");
		return SUCCESS;
	}

	public static int reloadBanItem(CommandContext<CommandSourceStack> context) {
		BanItemConfig.I.init();
		sendSuccess(context,"reload.success.ban_item");
		return SUCCESS;
	}

	public static int reloadReward(CommandContext<CommandSourceStack> context) {
		LoginReward.I.init();
		sendSuccess(context,"reload.success.reward");
		return SUCCESS;
	}

	public static int reward(CommandContext<CommandSourceStack> context) {
		if (LoginReward.I.getData().enable() && CommandConfig.I.getData().reward()) {
			ServerPlayer ser = context.getSource().getPlayer();
			if (ser == null) return FAILED;
			if (LoginRewardHelper.getRewards(ser)) return SUCCESS;
		}
		return FAILED;
	}

	public static int setHome(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getData().setHome()) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerConfig pd = PlayerConfig.get(player.getStringUUID());
				//player.level().dimension().toString();
				pd.setHome(PosData.create(player));
				sendSuccess(context,"command.set_home.success");
				return SUCCESS;
			}
		}
		sendFailure(context,"command.set_home.failed");
		return FAILED;
	}

	public static int home(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getData().home()) {


			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerConfig pd = PlayerConfig.get(player.getStringUUID());
				PosData home = pd.getData().getHome();
				if (home != null) {
					pd.addBack(player);
					if (_MF.tp(player, home)) {
						sendSuccess(context,"command.home.success");
						return SUCCESS;
					}
				}
			}
		}
		sendFailure(context,"command.home.failed");
		return FAILED;
	}

	public static int back(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getData().back()) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerConfig pd = PlayerConfig.get(player.getStringUUID());
				PosData back = pd.getBack();
				if (back != null) {
					if (_MF.tp(player, back)) {
						sendSuccess(context,"command.back.success");
						return SUCCESS;
					}
				}
			}
		}
		sendFailure(context,"command.back.failed");
		return FAILED;
	}

	private static final HashMap<String, UUID> tpaQueue = new HashMap<>();

	public static int tpa(CommandContext<CommandSourceStack> context, ServerPlayer targetPlayer) {
		if (CommandConfig.I.getData().tpa()) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null && targetPlayer != null) {
				tpaQueue.put(targetPlayer.getStringUUID(), player.getUUID());
				sendSuccess(context,"command.tpa.success");
				HashMap<String, Object> map = new HashMap<>();
				map.put("_tpa_send_player", player);
				MsgHelper.sendWithArgs(targetPlayer, Language.translatable(targetPlayer,"command.tpa.msg"), map);
				return SUCCESS;
			}
		}
		sendFailure(context,"command.tpa.failed");
		return FAILED;
	}

	public static int tpaAccept(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getData().tpaAccept()) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				if (tpaQueue.containsKey(player.getStringUUID())) {
					ServerPlayer targetPlayer = player.level().getServer().getPlayerList().getPlayer(tpaQueue.get(player.getStringUUID()));
					if (targetPlayer != null) {
						PlayerConfig.get(targetPlayer.getStringUUID()).addBack(targetPlayer);
						targetPlayer.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), Relative.ALL, player.getYRot(), player.getXRot(), true);
						sendSuccess(context,"command.tpa_accept.success");
						tpaQueue.remove(player.getStringUUID());
						return SUCCESS;
					}
				}
			}
		}
		sendFailure(context,"command.tpa_accept.failed");
		return FAILED;
	}

	public static int tpaDeny(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getData().tpaDeny()) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				if (tpaQueue.containsKey(player.getStringUUID())) {
					tpaQueue.remove(player.getStringUUID());
					sendSuccess(context,"command.tpa_deny.success");
					return SUCCESS;
				}
			}
		}
		sendFailure(context,"command.tpa_deny.failed");
		return FAILED;
	}


	public static void sendSuccess(CommandContext<CommandSourceStack> context, String msg){
		context.getSource().sendSuccess(()->Language.getComponent(context.getSource().getPlayer(),msg),false);
	}
	public static void sendFailure(CommandContext<CommandSourceStack> context, String msg){
		context.getSource().sendFailure(Language.getComponent(context.getSource().getPlayer(),msg));
	}
}

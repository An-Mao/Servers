package dev.anye.mc.st.helper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.config.ban$item.BanItemConfig;
import dev.anye.mc.st.config.black$list.BlackListConfig;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.login$reward.LoginReward;
import dev.anye.mc.st.config.msg.MsgConfig;
import dev.anye.mc.st.config.player$data.PlayerData;
import dev.anye.mc.st.config.player$group.PlayerGroupConfig;
import dev.anye.mc.st.data$type.PosData;
import dev.anye.mc.st.menu.LoginMenu;
import dev.anye.mc.st.menu.TrashBinContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Relative;

import java.util.HashMap;
import java.util.UUID;

public class CommandHelper {
	private static final int FAILED = 0;
	private static final int SUCCESS = Command.SINGLE_SUCCESS;

	public static int trash(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getDatas().trash) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player == null) return FAILED;
			if (ClearHelper.isClearTrashBin) {
				context.getSource().sendSuccess(() -> Language.getComponent("trash.command.error.cleaning"), false);
				return SUCCESS;
			}
			if (TrashBinContainer.nowPlayer != null) {
				context.getSource().sendSuccess(() -> Language.getComponent("trash.command.error.has_player"), false);
				return SUCCESS;
			}
			TrashBinContainer.nowPlayer = player;
			player.openMenu(new SimpleMenuProvider(
					(id, playerInventory, playerEntity) -> new TrashBinContainer(id, playerInventory),
					Language.getComponent("trash.menu.title")
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
					Language.getComponent("password.menu.title")
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
		MsgConfig.firstJoin.init();
		MsgConfig.everyDayJoin.init();
		MsgConfig.everyJoin.init();
		Language.I.init();
		BlackListConfig.instance.init();
		BanItemConfig.I.init();
		LoginReward.I.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.all"), false);
		return SUCCESS;
	}

	public static int reloadConfig(CommandContext<CommandSourceStack> context) {
		Config.I.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.config"), false);
		return SUCCESS;
	}

	public static int reloadPlayerGroup(CommandContext<CommandSourceStack> context) {
		PlayerGroupConfig.GROUPS.clear();
		PlayerGroupConfig.GROUPS.putAll(PlayerGroupConfig.getGroups());
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.player_group"), false);
		return SUCCESS;
	}

	public static int reloadClear(CommandContext<CommandSourceStack> context) {
		ClearConfig.ENTITY_CLEAR.init();
		ClearConfig.ITEM_CLEAR.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.clear"), false);
		return SUCCESS;
	}

	public static int reloadMsg(CommandContext<CommandSourceStack> context) {
		MsgConfig.firstJoin.init();
		MsgConfig.everyDayJoin.init();
		MsgConfig.everyJoin.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.msg"), false);
		return SUCCESS;
	}

	public static int reloadLanguage(CommandContext<CommandSourceStack> context) {
		Language.I.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.language"), false);
		return SUCCESS;
	}

	public static int reloadBlackList(CommandContext<CommandSourceStack> context) {
		BlackListConfig.instance.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.black_list"), false);
		return SUCCESS;
	}

	public static int reloadBanItem(CommandContext<CommandSourceStack> context) {
		BanItemConfig.I.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.ban_item"), false);
		return SUCCESS;
	}

	public static int reloadReward(CommandContext<CommandSourceStack> context) {
		LoginReward.I.init();
		context.getSource().sendSuccess(() -> Language.getComponent("reload.success.reward"), false);
		return SUCCESS;
	}

	public static int reward(CommandContext<CommandSourceStack> context) {
		if (LoginReward.I.getDatas().enable && CommandConfig.I.getDatas().reward) {
			ServerPlayer ser = context.getSource().getPlayer();
			if (ser == null) return FAILED;
			if (LoginRewardHelper.getRewards(ser)) return SUCCESS;
		}
		return FAILED;
	}

	public static int setHome(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getDatas().setHome) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerData pd = PlayerData.get(player.getStringUUID());
				//player.level().dimension().toString();
				pd.setHome(PosData.create(player));
				context.getSource().sendSuccess(() -> Language.getComponent("command.set_home.success"), false);
				return SUCCESS;
			}
		}
		context.getSource().sendFailure(Language.getComponent("command.set_home.failed"));
		return FAILED;
	}

	public static int home(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getDatas().home) {


			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerData pd = PlayerData.get(player.getStringUUID());
				PosData home = pd.getDatas().getHome();
				if (home != null) {
					pd.addBack(player);
					if (_MF.tp(player, home)) {
						context.getSource().sendSuccess(() -> Language.getComponent("command.home.success"), false);
						return SUCCESS;
					}
				}
			}
		}
		context.getSource().sendFailure(Language.getComponent("command.home.failed"));
		return FAILED;
	}

	public static int back(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getDatas().back) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerData pd = PlayerData.get(player.getStringUUID());
				PosData back = pd.getBack();
				if (back != null) {
					if (_MF.tp(player, back)) {
						context.getSource().sendSuccess(() -> Language.getComponent("command.back.success"), false);
						return SUCCESS;
					}
				}
			}
		}
		context.getSource().sendFailure(Language.getComponent("command.back.failed"));
		return FAILED;
	}

	private static final HashMap<String, UUID> tpaQueue = new HashMap<>();

	public static int tpa(CommandContext<CommandSourceStack> context, ServerPlayer targetPlayer) {
		if (CommandConfig.I.getDatas().tpa) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null && targetPlayer != null) {
				tpaQueue.put(targetPlayer.getStringUUID(), player.getUUID());
				context.getSource().sendSuccess(() -> Language.getComponent("command.tpa.success"), false);
				HashMap<String, Object> map = new HashMap<>();
				map.put("_tpa_send_player", player);
				MsgHelper.sendWithArgs(targetPlayer, Language.get("command.tpa.msg"), map);
				return SUCCESS;
			}
		}
		context.getSource().sendFailure(Language.getComponent("command.tpa.failed"));
		return FAILED;
	}

	public static int tpaAccept(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getDatas().tpaAccept) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				if (tpaQueue.containsKey(player.getStringUUID())) {
					ServerPlayer targetPlayer = player.level().getServer().getPlayerList().getPlayer(tpaQueue.get(player.getStringUUID()));
					if (targetPlayer != null) {
						PlayerData.get(targetPlayer.getStringUUID()).addBack(targetPlayer);
						targetPlayer.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), Relative.ALL, player.getYRot(), player.getXRot(), true);
						context.getSource().sendSuccess(() -> Language.getComponent("command.tpa_accept.success"), false);
						tpaQueue.remove(player.getStringUUID());
						return SUCCESS;
					}
				}
			}
		}
		context.getSource().sendFailure(Language.getComponent("command.tpa_accept.failed"));
		return FAILED;
	}

	public static int tpaDeny(CommandContext<CommandSourceStack> context) {
		if (CommandConfig.I.getDatas().tpaDeny) {
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				if (tpaQueue.containsKey(player.getStringUUID())) {
					tpaQueue.remove(player.getStringUUID());
					context.getSource().sendSuccess(() -> Language.getComponent("command.tpa_deny.success"), false);
					return SUCCESS;
				}
			}
		}
		context.getSource().sendFailure(Language.getComponent("command.tpa_deny.failed"));
		return FAILED;
	}

}

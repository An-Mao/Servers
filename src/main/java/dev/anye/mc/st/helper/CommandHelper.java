package dev.anye.mc.st.helper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.ban_item.BanItemConfig;
import dev.anye.mc.st.config.black_list.BlackListConfig;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.command.CommandData;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.config.login_reward.LoginReward;
import dev.anye.mc.st.config.login_reward.LoginRewardData;
import dev.anye.mc.st.config.msg.MsgConfig;
import dev.anye.mc.st.config.player$group.PlayerGroupConfig;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import dev.anye.mc.st.data_type.PosData;
import dev.anye.mc.st.menu.LoginMenu;
import dev.anye.mc.st.menu.STMenu;
import dev.anye.mc.st.menu.ShelfMenu;
import dev.anye.mc.st.menu.TrashBinContainer;
import dev.anye.mc.st.sys.currency.Currency;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Relative;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandHelper {
	private static final int FAILED = 0;
	private static final int SUCCESS = Command.SINGLE_SUCCESS;
	private static final Logger LOGGER = LogUtils.getLogger();

	public static int trash(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			TrashBinContainer.open(serverPlayer);
			return SUCCESS;
		}
		/*if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::trash))) {
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
		}*/
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
		Config.I.reload();
		PlayerGroupConfig.GROUPS.clear();
		PlayerGroupConfig.GROUPS.putAll(PlayerGroupConfig.getGroups());
		ClearConfig.ENTITY_CLEAR.reload();
		ClearConfig.ITEM_CLEAR.reload();
		MsgConfig.FIRST_JOIN.reload();
		MsgConfig.EVERY_DAY_JOIN.reload();
		MsgConfig.EVERY_JOIN.reload();
		Language.reloadLanguage();
		BlackListConfig.INSTANCE.reload();
		BanItemConfig.I.reload();
		LoginReward.I.reload();
		sendSuccess(context,"reload.success.all");
		return SUCCESS;
	}

	public static int reloadConfig(CommandContext<CommandSourceStack> context) {
		Config.I.reload();
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
		ClearConfig.ENTITY_CLEAR.reload();
		ClearConfig.ITEM_CLEAR.reload();
		sendSuccess(context,"reload.success.clear");
		return SUCCESS;
	}

	public static int reloadMsg(CommandContext<CommandSourceStack> context) {
		MsgConfig.FIRST_JOIN.reload();
		MsgConfig.EVERY_DAY_JOIN.reload();
		MsgConfig.EVERY_JOIN.reload();
		sendSuccess(context,"reload.success.msg");
		return SUCCESS;
	}

	public static int reloadLanguage(CommandContext<CommandSourceStack> context) {
		Language.reloadLanguage();
		sendSuccess(context,"reload.success.language");
		return SUCCESS;
	}

	public static int reloadBlackList(CommandContext<CommandSourceStack> context) {
		BlackListConfig.INSTANCE.reload();
		sendSuccess(context,"reload.success.black_list");
		return SUCCESS;
	}

	public static int reloadBanItem(CommandContext<CommandSourceStack> context) {
		BanItemConfig.I.reload();
		sendSuccess(context,"reload.success.ban_item");
		return SUCCESS;
	}

	public static int reloadReward(CommandContext<CommandSourceStack> context) {
		LoginReward.I.reload();
		sendSuccess(context,"reload.success.reward");
		return SUCCESS;
	}

	public static int reward(CommandContext<CommandSourceStack> context) {


		if (LoginReward.I.read(LoginRewardData::enable,false) && CommandConfig.I.read(CommandData::reward,false)) {
			ServerPlayer ser = context.getSource().getPlayer();
			if (ser == null) return FAILED;
			if (LoginRewardHelper.getRewards(ser)) return SUCCESS;
		}
		return FAILED;
	}

	public static int setHome(CommandContext<CommandSourceStack> context) {
		if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::setHome))) {
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
		if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::home))) {


			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				PlayerConfig pd = PlayerConfig.get(player.getStringUUID());
				PosData home = pd.read(playerData -> playerData.home);
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
		if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::back))) {
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
		if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::tpa))) {
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
		if (Boolean.TRUE.equals(CommandConfig.I.read(CommandData::tpaAccept))) {
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
		return CommandConfig.I.read(commandData -> {
			if (commandData.tpaDeny()){
				ServerPlayer player = context.getSource().getPlayer();
				if (player != null && tpaQueue.containsKey(player.getStringUUID())) {
					tpaQueue.remove(player.getStringUUID());
					sendSuccess(context, "command.tpa_deny.success");
					return SUCCESS;
				}
			}
			sendFailure(context,"command.tpa_deny.failed");
			return FAILED;
		},FAILED);
	}


	public static void sendSuccess(CommandContext<CommandSourceStack> context, String msg){
		context.getSource().sendSuccess(()->Language.getComponent(context.getSource().getPlayer(),msg),false);
	}
	public static void sendFailure(CommandContext<CommandSourceStack> context, String msg){
		context.getSource().sendFailure(Language.getComponent(context.getSource().getPlayer(),msg));
	}

	public static int sellItem(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			double price = DoubleArgumentType.getDouble(context,"price");
			if (price <= 0) {
				sendSuccess(context,"sell.command.item.failed");
				return FAILED;
			}

			if (Currency.I.sell(serverPlayer,serverPlayer.getMainHandItem(),price)){
				sendSuccess(context,"sell.command.item.success");
				return SUCCESS;
			}
			sendSuccess(context,"sell.command.item.failed");
		}
		return FAILED;
	}

	public static int playerItemShelf(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			ShelfMenu.open(serverPlayer,Currency.I.playerItemShelf());
			return SUCCESS;
		}
		return FAILED;
	}

	public static int my(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			PlayerCurrency.getPlayerCurrency(serverPlayer).read(data -> {
				Map<String,String> v = new HashMap<>();
				v.put("$uuid",serverPlayer.getStringUUID());
				v.put("$currency",data.currency() + Language.getComponent(serverPlayer,"currency.st.name").getString());
				MsgHelper.sendMsgToPlayerF(serverPlayer,"command.st.my",v);
			});
			return SUCCESS;
		}
		return FAILED;
	}

	public static int st(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			STMenu.open(serverPlayer);
			return SUCCESS;
		}
		return FAILED;
	}

	public static int outputHandItem(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			LOGGER.info("item data => {}",ItemHelper.itemToJson(serverPlayer.getMainHandItem(),serverPlayer.level().registryAccess()));
			return SUCCESS;
		}
		return FAILED;
	}

	public static int sellMob(CommandContext<CommandSourceStack> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
			double price = DoubleArgumentType.getDouble(context,"price");
			if (price > 0) {
				if (Currency.I.sellMob(serverPlayer, price)) {
					sendSuccess(context, "sell.command.mob.success");
					return SUCCESS;
				}
			}
			sendSuccess(context,"sell.command.mob.failed");
		}
		return FAILED;
	}
}

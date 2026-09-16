package dev.anye.mc.st.event;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.config.ban_item.BanItemConfig;
import dev.anye.mc.st.config.black_list.BlackListConfig;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.login.LoginConfig;
import dev.anye.mc.st.config.login.LoginData;
import dev.anye.mc.st.config.msg.MsgConfig;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import dev.anye.mc.st.helper.CommandHelper;
import dev.anye.mc.st.helper.CommandList;
import dev.anye.mc.st.helper.LoginHelper;
import dev.anye.mc.st.sys.Currency;
import dev.anye.mc.st.sys.EnchantmentExtract;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AnvilCraftEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

//@OnlyIn(Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = ST.MOD_ID)
public class GameEvent {
	@SubscribeEvent
	public static void onDamageFirst(EntityInvulnerabilityCheckEvent event) {
		if (Boolean.TRUE.equals(LoginConfig.INSTANCE.read(LoginData::enable))) {
			if (event.getEntity() instanceof ServerPlayer serverPlayer) {
				if (!LoginHelper.isLogin(serverPlayer)) event.setInvulnerable(true);
			}
		}
	}

	@SubscribeEvent
	public static void onPickup(ItemEntityPickupEvent.Pre event) {
		if (!event.getItemEntity().level().isClientSide() && BanItemConfig.I.checkItemAndSend(event.getItemEntity())) {
			event.getItemEntity().discard();
			event.setCanPickup(TriState.FALSE);
		}
	}

	@SubscribeEvent
	public static void regCommand(RegisterCommandsEvent event) {
		CommandList commandList = new CommandList(event.getDispatcher());
		commandList.register();
		event.getDispatcher().register(Commands.literal("tpa").then(
				Commands.argument("player", EntityArgument.player()).executes(context -> CommandHelper.tpa(context, EntityArgument.getPlayer(context, "player"))))
				);
		event.getDispatcher().register(Commands.literal("lang").then(
				Commands.argument("language", StringArgumentType.string()).executes(context -> {
					if (context.getSource().getPlayer() instanceof ServerPlayer serverPlayer){
						String lang = StringArgumentType.getString(context,"language");
						PlayerConfig config = PlayerConfig.get(serverPlayer);
						if (!Language.containsLanguage(lang + _SuffixCDT.JSON_SUFFIX)){
							config.sendMessage(serverPlayer,"set_lang.command.warnning");
						}
						config.setLang(lang);
						CommandHelper.sendSuccess(context,"set_lang.command.success");
						return 1;
					}
					CommandHelper.sendSuccess(context,"set_lang.command.failed");
					return 0;
				})));
		event.getDispatcher().register(Commands.literal("sell").then(Commands.literal("item").then(Commands.argument("price", DoubleArgumentType.doubleArg(0)).executes(CommandHelper::sellItem))));
	}

	public static int WAIT = 0;

	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent event) {
		if (event.getEntity().level().isClientSide()) return;
		if (event.getEntity() instanceof ItemEntity itemEntity && BanItemConfig.I.checkItemAndSend(itemEntity)) {
			event.setCanceled(true);
			return;
		}

		if (WAIT > 0) {
			WAIT--;
			return;
		}
		ClearConfig.ENTITY_CLEAR.clear(event);
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			CommandConfig.I.read(commandData -> {
				if (commandData.back()) {
					PlayerConfig.get(serverPlayer).addBack(serverPlayer);
				}
			});
		}else {
			if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer){
				Currency.I.entity(serverPlayer,event.getEntity());
			}
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			BlackListConfig.INSTANCE.check(serverPlayer);
			LoginConfig.INSTANCE.openLogin(serverPlayer);
			MsgConfig.FIRST_JOIN.send(serverPlayer);
			MsgConfig.EVERY_DAY_JOIN.send(serverPlayer);
			MsgConfig.EVERY_JOIN.send(serverPlayer);
		}
	}

	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			LoginConfig.INSTANCE.read(loginData -> {
				if (loginData.enable()) {
					LoginHelper.removeLogin(serverPlayer);
				}
			});
		}
	}


	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (LoginHelper.checkLogin(serverPlayer)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (LoginHelper.checkLogin(serverPlayer)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onInteractSpecific(PlayerInteractEvent.EntityInteract event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (LoginHelper.checkLogin(serverPlayer)) {
				event.setCanceled(true);
			}
		}
	}

	public static void onInteraction(PlayerInteractEvent.RightClickEmpty event) {
		if (LoginHelper.checkLogin((ServerPlayer) event.getEntity())) {
			//event..setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (LoginHelper.checkLogin(serverPlayer)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (LoginHelper.checkLogin(serverPlayer)) {
				event.setCanceled(true);
			}
		}
	}

	public static void onInteraction(PlayerInteractEvent.LeftClickEmpty event) {
		if (LoginHelper.checkLogin((ServerPlayer) event.getEntity())) {
			//event.setCanceled(true);
		}
	}
	@SubscribeEvent
	public static void onAnvil(AnvilUpdateEvent event){
		if (event.getPlayer() instanceof ServerPlayer) {
			Config.I.read(configData -> {
				if (configData.enchantmentExtract) {
					EnchantmentExtract.onUpdate(event, event.getLeft(), event.getRight());
				}
			});
		}
	}



	//@SubscribeEvent
	public static void onAnvil(AnvilCraftEvent.Post event){
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			Config.I.read(configData -> {
				if (configData.enchantmentExtract) {
					EnchantmentExtract.onTake(serverPlayer, event.getLeft(), event.getRight());
				}
			});
		}
	}
}

package dev.anye.mc.st.event;

import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.black$list.BlackListConfig;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.login.LoginConfig;
import dev.anye.mc.st.config.msg.MsgConfig;
import dev.anye.mc.st.config.player$data.PlayerData;
import dev.anye.mc.st.data$type.ClearList;
import dev.anye.mc.st.helper.*;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

//@OnlyIn(Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = ST.MOD_ID)
public class GameEvent {
	@SubscribeEvent
	public static void onDamageFirst(EntityInvulnerabilityCheckEvent event) {
		if (LoginConfig.INSTANCE.getDatas().enable) {
			if (event.getEntity() instanceof ServerPlayer serverPlayer) {
				if (!LoginHelper.isLogin(serverPlayer)) event.setInvulnerable(true);
			}
		}
	}

	@SubscribeEvent
	public static void onPickup(ItemEntityPickupEvent.Pre event) {
		if (!event.getItemEntity().level().isClientSide() && BanItemHelper.checkItemAndSend(event.getItemEntity())) {
			event.getItemEntity().discard();
			event.setCanPickup(TriState.FALSE);
		}
	}

	@SubscribeEvent
	public static void regCommand(RegisterCommandsEvent event) {
		CommandList commandList = new CommandList(event.getDispatcher());
		commandList.register();
		event.getDispatcher()
				.register(Commands.literal("tpa")
						.then(Commands.argument("player", EntityArgument.player())
								.executes(context -> CommandHelper.tpa(context, EntityArgument.getPlayer(context, "player")))));
	}

	public static int wait = 0;

	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent event) {
		if (event.getEntity().level().isClientSide()) return;
		if (BanItemHelper.checkItemAndSend(event.getEntity())) {
			event.setCanceled(true);
			return;
		}

		if (wait > 0) {
			wait--;
			return;
		}
		if (event.getEntity() instanceof LivingEntity livingEntity) {
			if (ClearConfig.ENTITY_CLEAR.getDatas().enable) {
				int entityLimit = ClearConfig.ENTITY_CLEAR.getEntityLimit(livingEntity);
				if (entityLimit == 0 || ClearConfig.ENTITY_CLEAR.getDatas().allEntityLimit == 0) return;
				if (ClearConfig.ENTITY_CLEAR.isInWhiteList(livingEntity)) return;
				if (livingEntity.level() instanceof ServerLevel serverLevel) {
					ClearList clearList = new ClearList();
					ClearList allClearList = new ClearList();
					int[] count = {0, 0, 0};
					Iterable<Entity> entities = serverLevel.getAllEntities();
					for (Entity entity : entities) {
						if (entity instanceof ServerPlayer) continue;
						allClearList.add(entity);
						count[0]++;
						if (entity.getType() == livingEntity.getType()) {
							clearList.add(entity);
							if (count[1] >= entityLimit) {
								count[2] = 1;
								break;
							}
							count[1]++;
						}
						if (count[0] >= ClearConfig.ENTITY_CLEAR.getDatas().allEntityLimit) {
							count[2] = 2;
							break;
						}
					}
					if (count[2] == 1) {
						if (ClearConfig.ENTITY_CLEAR.getDatas().stopSpawn) event.setCanceled(true);
						MsgHelper.sendServerMsg(serverLevel, ClearConfig.ENTITY_CLEAR.getDatas().LimitClearMsg);
						MsgHelper.sendServerMsg(serverLevel.getServer(), ClearConfig.ENTITY_CLEAR.getMsg(0), ClearHelper.pullClearCount(clearList.clearEntity()));
					} else if (count[2] == 2) {
						wait = 200;
						if (ClearConfig.ENTITY_CLEAR.getDatas().stopSpawn) event.setCanceled(true);
						MsgHelper.sendServerMsg(serverLevel, ClearConfig.ENTITY_CLEAR.getDatas().LimitClearMsg);
						MsgHelper.sendServerMsg(serverLevel.getServer(), ClearConfig.ENTITY_CLEAR.getMsg(0), ClearHelper.pullClearCount(allClearList.clearEntityWithCheck()));

					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (CommandConfig.I.getDatas().back) {
				PlayerData.get(serverPlayer.getStringUUID()).addBack(serverPlayer);
			}
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (BlackListConfig.instance.getDatas().enable) {
				boolean has = PlayerHelper.checkPlayer(serverPlayer, BlackListConfig.instance.getDatas().list);
				if (BlackListConfig.instance.getDatas().allowedMode) {
					if (!has)
						serverPlayer.connection.disconnect(Component.literal(BlackListConfig.instance.getDatas().msg));
				} else {
					if (has)
						serverPlayer.connection.disconnect(Component.literal(BlackListConfig.instance.getDatas().msg));
				}

			}
			if (LoginConfig.INSTANCE.getDatas().enable) LoginHelper.openLogin(serverPlayer);
			if (MsgConfig.firstJoin.getDatas().isEnable()) MsgConfig.firstJoin.send(serverPlayer);
			if (MsgConfig.everyDayJoin.getDatas().isEnable()) MsgConfig.everyDayJoin.send(serverPlayer);
			if (MsgConfig.everyJoin.getDatas().isEnable()) MsgConfig.everyJoin.send(serverPlayer);

		}
	}

	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			if (LoginConfig.INSTANCE.getDatas().enable) {
				LoginHelper.removeLogin(serverPlayer);
			}
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
	public static void onInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
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
}

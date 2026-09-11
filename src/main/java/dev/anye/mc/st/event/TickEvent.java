package dev.anye.mc.st.event;

import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.login.LoginConfig;
import dev.anye.mc.st.helper.ClearHelper;
import dev.anye.mc.st.helper.LoginHelper;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;

@EventBusSubscriber(modid = ST.MOD_ID)
public class TickEvent {
	@SubscribeEvent
	public static void onStart(ServerStartedEvent event) {
		ClearConfig.start();
	}

	@SubscribeEvent
	public static void onStop(ServerStoppingEvent event) {
		ClearConfig.stop();
	}

	@SubscribeEvent
	public static void onTick(ServerTickEvent.Pre event) {
		ClearConfig.tick(event.getServer());
	}

	private static final HashMap<String, Integer> loginTime = new HashMap<>();

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer && LoginHelper.checkLogin(serverPlayer)) {
			String uuid = serverPlayer.getStringUUID();
			loginTime.put(uuid, loginTime.getOrDefault(uuid, 0) + 1);
			if (loginTime.get(uuid) >= LoginConfig.INSTANCE.getData().time()) {
				serverPlayer.connection.disconnect(Language.getComponent(serverPlayer,"login.failed"));
				loginTime.remove(uuid);
			}
		}

	}


	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Post event) {
		if (event.getEntity().level().isClientSide()) return;
		if (Config.I.getData().clearAnomalousEntity) {
			if (event.getEntity() instanceof LivingEntity livingEntity) {
				if (livingEntity.getPose().equals(Pose.DYING)) {
					if (livingEntity.deathTime > 20) {
						livingEntity.remove(Entity.RemovalReason.DISCARDED);
					}
				}
			}
		}
	}
}

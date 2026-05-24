package dev.anye.mc.st.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.listen.ListenConfig;
import dev.anye.mc.st.config.login.LoginConfig;
import dev.anye.mc.st.helper.ClearHelper;
import dev.anye.mc.st.helper.LoginHelper;
import dev.anye.mc.st.helper.MsgHelper;
import dev.anye.mc.st.listen.Listen;

import java.util.HashMap;

@EventBusSubscriber(modid = ST.MOD_ID)
public class TickEventS {
    private static boolean runThread = false;
    private static int time = 0;
    private static int itemTime = 0;
    private static boolean clearEntity = false;
    private static boolean clearItem = false;
    private static final String[] msg = {"",""};

    private static final Thread tt = new Thread(() -> {
        while (runThread) {
            try {
                if (!clearItem && ClearConfig.ITEM_CLEAR.getDatas().enable && ClearConfig.ITEM_CLEAR.getDatas().autoClearTime > 0) {
                    if (itemTime < ClearConfig.ITEM_CLEAR.getDatas().autoClearTime) {
                        int msgIndex = ClearConfig.ITEM_CLEAR.getDatas().autoClearTime - itemTime;
                        if (ClearConfig.ITEM_CLEAR.getDatas().msg.containsKey(msgIndex))
                            msg[1] = ClearConfig.ITEM_CLEAR.getDatas().msg.getOrDefault(msgIndex, "");
                        itemTime++;
                    } else {
                        itemTime = 0;
                        clearItem = true;
                    }
                }
                if (!clearEntity && ClearConfig.ENTITY_CLEAR.getDatas().enable && ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime > 0) {
                    if (time < ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime) {
                        int msgIndex = ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime - time;
                        if (ClearConfig.ENTITY_CLEAR.getDatas().msg.containsKey(msgIndex))
                            msg[0] = ClearConfig.ENTITY_CLEAR.getDatas().msg.getOrDefault(msgIndex, "");
                        time++;
                    } else {
                        time = 0;
                        clearEntity = true;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        time = 0;
        itemTime = 0;
        clearEntity = false;
        clearItem = false;
        msg[0] = "";
        msg[1] = "";
    });
    private static boolean ThreadIsRun = false;
    @SubscribeEvent
    public static void onStart(ServerStartedEvent event){
        runThread = true;
        if (!ThreadIsRun) {
            ThreadIsRun = true;
            tt.start();
        }
        //if (ListenConfig.I.getDatas().isEnable()) Listen.I.start();
    }
    @SubscribeEvent
    public static void onStop(ServerStoppingEvent event){
        runThread = false;
        Listen.I.close();
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Pre event){
        if (clearEntity) {
            ClearHelper.clearServerEntity(event.server());
            clearEntity = false;
        }
        if (clearItem) {
            ClearHelper.clearItem(event.server());
            clearItem = false;
        }
        for (int i = 0; i < 2; i++) {
            if (!msg[i].isEmpty()) {
                MsgHelper.sendServerMsg(event.server(),msg[i]);
                msg[i] = "";
            }
        }
    }
    private static final HashMap<String,Integer> loginTime = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event){
            if (event.player() instanceof ServerPlayer serverPlayer && LoginHelper.checkLogin(serverPlayer)){
                String uuid = serverPlayer.getStringUUID();
                loginTime.put(uuid,loginTime.getOrDefault(uuid,0)+1);
                if (loginTime.get(uuid) >= LoginConfig.INSTANCE.getDatas().time){
                    serverPlayer.connection.disconnect(Language.getComponent("login.failed"));
                    loginTime.remove(uuid);
                }
            }

    }


    @SubscribeEvent
    public static void onEntityTick(LevelTickEvent.Post event){
        if (event.side().isClient()) return;
        if (Config.I.getDatas().clearAnomalousEntity) {
			if (event.level() instanceof ServerLevel serverLevel) {
				serverLevel.getEntities().getAll().forEach(e -> {
					if (e instanceof LivingEntity livingEntity) {
						if (livingEntity.getPose().equals(Pose.DYING)) {
							if (livingEntity.deathTime > 20) {
								livingEntity.remove(Entity.RemovalReason.DISCARDED);
							}
						}
					}
				});
			}
        }
    }
}

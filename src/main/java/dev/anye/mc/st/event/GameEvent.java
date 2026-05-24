package dev.anye.mc.st.event;

import dev.anye.mc.st.helper.*;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.black$list.BlackListConfig;
import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.login.LoginConfig;
import dev.anye.mc.st.config.msg.MsgConfig;
import dev.anye.mc.st.config.player$data.PlayerData;
import dev.anye.mc.st.data$type.ClearList;

//@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(modid = ST.MOD_ID)
public class GameEvent {

    @SubscribeEvent(priority = Priority.HIGH)
    public static boolean onDamageFirst(LivingHurtEvent event) {
        if (LoginConfig.INSTANCE.getDatas().enable) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                if (!LoginHelper.isLogin(serverPlayer)) return true;
            }
        }
		return false;
    }

    @SubscribeEvent(priority = Priority.HIGH)
    public static void onPickup(EntityItemPickupEvent event){
        if (!event.getItem().level().isClientSide() && BanItemHelper.checkItemAndSend(event.getItem())) {
            event.getItem().discard();
			event.setResult(Result.DENY);
            //event.setCanPickup(TriState.FALSE)
        }
    }


    @SubscribeEvent
    public static void regCommand(RegisterCommandsEvent event) {
        CommandList commandList = new CommandList(event.getDispatcher());
        commandList.register();
        event.getDispatcher()
                .register(Commands.literal("tpa")
                        .then(Commands.argument("player",EntityArgument.player())
                                .executes(context -> CommandHelper.tpa(context, EntityArgument.getPlayer(context, "player")))));
    }
    public static int wait = 0;

    @SubscribeEvent(priority = Priority.HIGH)
    public static boolean onJoin(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide()) return false;
        if (BanItemHelper.checkItemAndSend(event.getEntity())) {
            return true;
        }

        if (wait > 0) {
            wait--;
            return false;
        }
        if (event.getEntity() instanceof LivingEntity livingEntity){
            if (ClearConfig.ENTITY_CLEAR.getDatas().enable){
                int entityLimit = ClearConfig.ENTITY_CLEAR.getEntityLimit(livingEntity);
                if (entityLimit == 0 || ClearConfig.ENTITY_CLEAR.getDatas().allEntityLimit == 0) return false;
                if (ClearConfig.ENTITY_CLEAR.isInWhiteList(livingEntity)) return false;
                if (livingEntity.level() instanceof ServerLevel serverLevel){
                    ClearList clearList = new ClearList();
                    ClearList allClearList = new ClearList();
                    int[] count ={0,0,0};
                    Iterable<Entity> entities = serverLevel.getAllEntities();
                    for (Entity entity : entities) {
                        if (entity instanceof ServerPlayer) continue;
                        allClearList.add(entity);
                        count[0]++;
                        if (entity.getType() == livingEntity.getType()) {
                            clearList.add(entity);
                            if (count[1] >= entityLimit){
                                count[2] = 1;
                                break;
                            }
                            count[1]++;
                        }
                        if (count[0] >= ClearConfig.ENTITY_CLEAR.getDatas().allEntityLimit){
                            count[2] = 2;
                            break;
                        }
                    }
                    if (count[2] == 1) {
                        if (ClearConfig.ENTITY_CLEAR.getDatas().stopSpawn) return true;
                        MsgHelper.sendServerMsg(serverLevel, ClearConfig.ENTITY_CLEAR.getDatas().LimitClearMsg);
                        MsgHelper.sendServerMsg(serverLevel.getServer(), ClearConfig.ENTITY_CLEAR.getMsg(0), ClearHelper.pullClearCount(clearList.clearEntity()));
                    }else if (count[2] == 2) {
                        wait = 200;
                        if (ClearConfig.ENTITY_CLEAR.getDatas().stopSpawn) return true;
                        MsgHelper.sendServerMsg(serverLevel, ClearConfig.ENTITY_CLEAR.getDatas().LimitClearMsg);
                        MsgHelper.sendServerMsg(serverLevel.getServer(), ClearConfig.ENTITY_CLEAR.getMsg(0), ClearHelper.pullClearCount(allClearList.clearEntityWithCheck()));

                    }
                }
            }
        }
		return false;
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (CommandConfig.I.getDatas().back){
                PlayerData.get(serverPlayer.getStringUUID()).addBack(serverPlayer);
            }
        }
    }
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
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
            if (LoginConfig.INSTANCE.getDatas().enable){
                LoginHelper.removeLogin(serverPlayer);
            }
        }
    }


    @SubscribeEvent(priority = Priority.HIGH)
    public static boolean onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                return true;
            }
        }
		return false;
    }
    @SubscribeEvent(priority = Priority.HIGH)
    public static boolean onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                return true;
            }
        }
		return false;
    }
    @SubscribeEvent(priority = Priority.HIGH)
    public static boolean onInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                return true;
            }
        }
		return false;
    }

    /*
    private static void onInteraction(PlayerInteractEvent.RightClickEmpty event){
        if (LoginHelper.checkLogin((ServerPlayer) event.getEntity())) {
            //event..setCanceled(false);
        }
    }

     */
    @SubscribeEvent(priority = Priority.HIGH)
    public static boolean onRightClickItem(PlayerInteractEvent.RightClickItem event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                return true;
            }
        }
		return false;
    }
	/*
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                event.setCanceled(false);
            }
        }
    }
    private static void onInteraction(PlayerInteractEvent.LeftClickEmpty event){
        if (LoginHelper.checkLogin((ServerPlayer) event.getEntity())) {
            //event.setCanceled(false);
        }
    }
	*/
}

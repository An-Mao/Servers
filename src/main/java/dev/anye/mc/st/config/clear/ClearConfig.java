package dev.anye.mc.st.config.clear;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.core.system.task.TimingWheel;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.data_type.ClearList;
import dev.anye.mc.st.event.GameEvent;
import dev.anye.mc.st.helper.ClearHelper;
import dev.anye.mc.st.helper.EntityHelper;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.concurrent.TimeUnit;

public class ClearConfig {
	public static final EntityClear ENTITY_CLEAR = new EntityClear();
	public static final ItemClear ITEM_CLEAR = new ItemClear();

	private static boolean ThreadIsRun = false;
	private static boolean runThread = false;
	private static int time = 0;
	private static int itemTime = 0;
	private static boolean clearEntity = false;
	private static boolean clearItem = false;
	private static final String[] msg = {"", ""};

	private static boolean lock = false;

	private static TimingWheel.TimerTask clearThread = null;

	private ClearConfig(){}

	public static void thread() {
		if (!clearItem) {
			ClearConfig.item();
		}
		if (!clearEntity) {
			ClearConfig.entity();
		}
	}


	public static void start(){
/*		if (!){
			new Thread(() -> {
				while (runThread) {
					try {
						if (!clearItem){
							ClearConfig.item();
						}

						if (!clearEntity){
							ClearConfig.entity();
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
		}*/
		if (lock) return;
		lock = true;
		if (clearThread == null){
			clearThread = ST.TIMING_WHEEL.schedule(ClearConfig::thread,1, TimeUnit.SECONDS);
		}
		lock = false;
	}

	public static void stop(){
		if (lock)return;
		lock = true;
		clearThread.cancel();
		clearThread = null;


		time = 0;
		itemTime = 0;
		clearEntity = false;
		clearItem = false;
		msg[0] = "";
		msg[1] = "";

		lock = false;
	}

	public static void item(){
		ITEM_CLEAR.read(itemClearData -> {
			if (itemClearData.enable() && itemClearData.autoClearTime() > 0) {
				if (itemTime < itemClearData.autoClearTime()) {
					int msgIndex = itemClearData.autoClearTime() - itemTime;
					if (itemClearData.msg().containsKey(msgIndex))
						msg[1] = itemClearData.msg().getOrDefault(msgIndex, "");
					itemTime++;
				} else {
					itemTime = 0;
					clearItem = true;
				}
			}
		});
	}

	public static void entity(){
		ENTITY_CLEAR.read(entityClearData -> {
			if (entityClearData.enable() && entityClearData.autoClearTime() > 0) {
				if (time < entityClearData.autoClearTime()) {
					int msgIndex = entityClearData.autoClearTime() - time;
					if (entityClearData.msg().containsKey(msgIndex))
						msg[0] = entityClearData.msg().getOrDefault(msgIndex, "");
					time++;
				} else {
					time = 0;
					clearEntity = true;
				}
			}
		});
	}

	public static void tick(MinecraftServer server){
		if (clearEntity) {
			ClearHelper.clearServerEntity(server);
			clearEntity = false;
		}
		if (clearItem) {
			ClearHelper.clearItem(server);
			clearItem = false;
		}
		for (int i = 0; i < 2; i++) {
			if (!msg[i].isEmpty()) {
				MsgHelper.sendServerMsg(server, msg[i]);
				msg[i] = "";
			}
		}
	}

	public static class EntityClear extends _JsonConfigS<EntityClearData> {
		private static final String FILE = _File.getFilePath(ConfigDir.CLEAR, "entity.json");

		public EntityClear() {
			super(FILE, EntityClearData.DEFAULT, new TypeToken<>() {});
		}

		public String getMsg(int time) {
			return read(entityClearData -> getMsg(entityClearData,time),"");
		}
		public static String getMsg(EntityClearData entityClearData,int time) {
			return entityClearData.msg().getOrDefault(time, "");
		}
		public static int getEntityLimit(EntityClearData entityClearData,Entity entity) {
			return entityClearData.entityLimit().getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(), entityClearData.defaultEntityLimit());
		}

		public static boolean isInWhiteList(EntityClearData entityClearData,Entity entity) {
			return entityClearData.whiteList().contains(EntityHelper.getEntityRegID(entity));
		}

		public boolean isInBlackList(Entity entity) {
			return read(entityClearData -> {
				if (entityClearData.enable()){
					return entityClearData.blackList().contains(EntityHelper.getEntityRegID(entity));
				}
				return false;
			});
		}


		public void clear(EntityJoinLevelEvent event){
			if (event.getEntity() instanceof LivingEntity livingEntity) {
				read(entityClearData -> {
					if (entityClearData.enable()) {
						int entityLimit = getEntityLimit(entityClearData,livingEntity);
						if (entityLimit == 0 || entityClearData.allEntityLimit() == 0 || isInWhiteList(entityClearData,livingEntity))
							return;

						if (livingEntity.level() instanceof ServerLevel serverLevel) {
							ClearList allClearList = new ClearList(entityClearData);
							ClearList clearList = new ClearList(entityClearData);
							switch (selectEntity(livingEntity, serverLevel, allClearList, entityClearData.allEntityLimit(), clearList, entityLimit)) {
								case 1: {
									if (entityClearData.stopSpawn()) event.setCanceled(true);
									MsgHelper.sendServerMsg(serverLevel, entityClearData.limitClearMsg());
									MsgHelper.sendServerMsg(serverLevel.getServer(), getMsg(entityClearData, 0), ClearHelper.pullClearCount(clearList.clearEntity()));
								}
								break;
								case 2: {
									GameEvent.WAIT = 200;
									if (entityClearData.stopSpawn()) event.setCanceled(true);
									MsgHelper.sendServerMsg(serverLevel, entityClearData.limitClearMsg());
									MsgHelper.sendServerMsg(serverLevel.getServer(), getMsg(entityClearData, 0), ClearHelper.pullClearCount(allClearList.clearEntityWithCheck()));
								}
								break;
								default:
									break;
							}
						}

					}
				});
			}
		}
		public int selectEntity(LivingEntity livingEntity,ServerLevel serverLevel,ClearList allClearList,int allEntityLimit,ClearList clearList,int entityLimit){
			Iterable<Entity> entities = serverLevel.getAllEntities();
			for (Entity entity : entities) {
				if (entity instanceof ServerPlayer) continue;
				allClearList.add(entity);
				if (entity.getType() == livingEntity.getType()) {
					clearList.add(entity);
					if (clearList.size() >= entityLimit) {
						return 1;
					}
				}
				if (allClearList.size() >= allEntityLimit) {
					return 2;
				}
			}
			return 0;
		}


	}

	public static class ItemClear extends _JsonConfig<ItemClearData> {
		private static final String FILE = _File.getFilePath(ConfigDir.CLEAR, "item.json");

		public ItemClear() {
			super(FILE, ItemClearData.DEFAULT, new TypeToken<>() {});
		}
	}
}

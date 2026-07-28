package dev.anye.mc.st.config.clear;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.data_type.ClearList;
import dev.anye.mc.st.event.GameEvent;
import dev.anye.mc.st.helper.ClearHelper;
import dev.anye.mc.st.helper.EntityHelper;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class ClearConfig {
	public static final EntityClear ENTITY_CLEAR = new EntityClear();
	public static final ItemClear ITEM_CLEAR = new ItemClear();

	private ClearConfig(){}


	public static class EntityClear extends _JsonConfig<EntityClearData> {
		private static final String FILE = _File.getFilePath(ConfigDir.CLEAR, "entity.json");

		public EntityClear() {
			super(FILE, EntityClearData.DEFAULT, new TypeToken<>() {});
		}

		public String getMsg(int time) {
			if (this.data.isPresent()){
				return data.get().msg().getOrDefault(time, "");
			}
			return "";
		}

		public int getEntityLimit(Entity entity) {
			if (this.data.isPresent()){
				return data.get().entityLimit().getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(), data.get().defaultEntityLimit());
			}
			return 0;
		}

		public boolean isInWhiteList(Entity entity) {
			if (this.data.isPresent()){
				return data.get().whiteList().contains(EntityHelper.getEntityRegID(entity));
			}
			return false;
		}

		public boolean isInBlackList(Entity entity) {
			if (this.data.isPresent()){
				return data.get().blackList().contains(EntityHelper.getEntityRegID(entity));
			}
			return false;
		}


		public void clear(EntityJoinLevelEvent event){
			ifPresent(entityClearData -> {
				if (entityClearData.enable() && event.getEntity() instanceof LivingEntity livingEntity) {
					int entityLimit = getEntityLimit(livingEntity);
					if (entityLimit == 0 || entityClearData.allEntityLimit() == 0 || isInWhiteList(livingEntity)) return;

					if (livingEntity.level() instanceof ServerLevel serverLevel) {
						ClearList allClearList = new ClearList(entityClearData);
						ClearList clearList = new ClearList(entityClearData);
						switch (selectEntity(livingEntity,serverLevel,allClearList,entityClearData.allEntityLimit(),clearList,entityLimit)){
							case 1 : {
								if (entityClearData.stopSpawn()) event.setCanceled(true);
								MsgHelper.sendServerMsg(serverLevel, entityClearData.limitClearMsg());
								MsgHelper.sendServerMsg(serverLevel.getServer(), getMsg(0), ClearHelper.pullClearCount(clearList.clearEntity()));
							}
							break;
							case 2 : {
								GameEvent.WAIT = 200;
								if (entityClearData.stopSpawn()) event.setCanceled(true);
								MsgHelper.sendServerMsg(serverLevel, entityClearData.limitClearMsg());
								MsgHelper.sendServerMsg(serverLevel.getServer(), getMsg(0), ClearHelper.pullClearCount(allClearList.clearEntityWithCheck()));
							}
							break;
							default:break;
						}
					}

				}
			});

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

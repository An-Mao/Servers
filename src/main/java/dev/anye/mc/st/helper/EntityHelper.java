package dev.anye.mc.st.helper;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public final class EntityHelper {
	private static final Logger LOGGER = LogUtils.getLogger();
	private EntityHelper(){}

	public static String getEntityRegStringID(Entity entity) {
		return getEntityRegID(entity).toString();
	}
	public static Identifier getEntityRegID(Entity entity) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
	}
	public static String getEntityRegStringIDWith(Entity entity,String c) {
		Identifier identifier = getEntityRegID(entity);
		return identifier.getNamespace() + c + identifier.getPath();
	}
	public static String getEntityRegStringIDWithX(Entity entity,String c) {
		Identifier identifier = getEntityRegID(entity);
		return identifier.getPath() + c + identifier.getNamespace();
	}


	/**
	 * 将实体序列化为JSON
	 * @param entity 要进行序列化的实体
	 * @return JSON
	 */
	public static JsonElement toJson(Entity entity) {
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
			TagValueOutput entityData = TagValueOutput.createWithContext(reporter, entity.registryAccess());
			entity.saveWithoutId(entityData);
			return CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, entityData.buildResult()).getOrThrow();
		}
	}

	/**
	 * 将JSON反序列化为实体实例
	 * @param serverLevel level
	 * @param eid 实体注册ID
	 * @param json 实体数据
	 * @return 成功返回实体实例，失败返回null
	 */
	public static Entity jsonToEntity(ServerLevel serverLevel, String eid, JsonElement json){
		return jsonToEntity(serverLevel,Identifier.parse(eid),json);
	}

	/**
	 * 将JSON反序列化为实体实例
	 * @param serverLevel level
	 * @param eid 实体注册ID
	 * @param json 实体数据
	 * @return 成功返回实体实例，失败返回null
	 */
	public static Entity jsonToEntity(ServerLevel serverLevel, Identifier eid,JsonElement json){
		Entity entity = BuiltInRegistries.ENTITY_TYPE.getValue(eid).create(serverLevel, EntitySpawnReason.COMMAND);
		if (entity != null) {
			try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
				CompoundTag tag = CompoundTag.CODEC.parse(JsonOps.INSTANCE,json).getOrThrow();
				entity.load(TagValueInput.create(reporter, serverLevel.registryAccess(), tag));
				return entity;
			}
		}
		return null;
	}

	public static ItemStack getSpawnEgg(EntityType<?> entityType,ItemStack or){
		return SpawnEggItem.byId(entityType).map(ItemStack::new).orElse(or);
	}
}

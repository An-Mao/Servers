package dev.anye.mc.st.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class EntityHelper {
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
}

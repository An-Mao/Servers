package dev.anye.mc.st.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;

public class EntityHelper {
    public static String getEntityRegID(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }
}

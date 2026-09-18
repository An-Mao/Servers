package dev.anye.mc.st.config.currency.shelf.entity;

import com.google.gson.JsonElement;
import dev.anye.mc.st.helper.EntityHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record ShelfEntityData(String playerUUID, double price,String eid, JsonElement data, long time) {

	public ShelfEntityData(ServerPlayer serverPlayer, double price, Entity entity, long time){
		this(serverPlayer.getStringUUID(),price,EntityHelper.getEntityRegStringID(entity), EntityHelper.toJson(entity),time);
	}
	public ShelfEntityData(ServerPlayer serverPlayer, double price, Entity entity){
		this(serverPlayer.getStringUUID(),price,EntityHelper.getEntityRegStringID(entity), EntityHelper.toJson(entity),System.currentTimeMillis());
	}

	public Entity getEntity(ServerPlayer serverPlayer){
		return EntityHelper.jsonToEntity(serverPlayer.level(),eid,data);
	}
	public EntityType<?> getEntityType(){
		return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(eid));
	}



}

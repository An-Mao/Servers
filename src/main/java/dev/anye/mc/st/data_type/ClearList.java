package dev.anye.mc.st.data_type;

import dev.anye.mc.st.config.clear.EntityClearData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClearList {
	private final @NotNull EntityClearData clearData;
	private final List<Entity> list = new ArrayList<>();

	public ClearList(@NotNull EntityClearData entityClearData) {
		//if (entityClearData == null) throw new IllegalArgumentException("EntityClearData is null");
		this.clearData = entityClearData;
	}


	public void add(Entity entity) {
		if (list.contains(entity) || !entity.isAlive()) return;
		list.add(entity);
	}

	public void clear() {
		list.clear();
	}

	public int size() {
		return list.size();
	}

	public int clearEntity() {
		int count = 0;
		for (Entity entity: list){
			if (entity.isAlive()) {
				clearEntity(entity);
				count++;
				entity.discard();
			}
		}
		return count;
	}

	public void clearEntity(Entity entity){
		if (clearData.safeEntityItem() && entity instanceof LivingEntity livingEntity) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				ItemStack itemStack = livingEntity.getItemBySlot(slot);
				if (!itemStack.isEmpty()) {
					livingEntity.level().addFreshEntity(new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), itemStack));
				}
				livingEntity.setItemSlot(slot, ItemStack.EMPTY);
			}
		}
	}

	public int clearEntityWithCheck() {
		int count = 0;
		for (Entity entity: list){
			if (entity.isAlive() && canClear(entity)) {
				clearEntity(entity);
				count++;
				entity.discard();
			}
		}
		return count;
	}

	public boolean canClear(Entity livingEntity) {
		if (clearData.whiteList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()))
			return false;
		if (clearData.blackList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()))
			return true;
		//if (serverLevel.getNearestPlayer(livingEntity, ClearConfig.ENTITY_CLEAR.getDatas().safeDistance) != null) return false;
		if (livingEntity.getCustomName() != null && !clearData.clearName())
			return false;
		if (livingEntity.getType().getCategory().equals(MobCategory.MONSTER) && !clearData.clearMob())
			return false;
		else if (livingEntity instanceof Npc && !clearData.clearNpc())
			return false;
		else if (livingEntity instanceof Animal && !clearData.clearAnimal())
			return false;
		else
			return !(livingEntity instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame() || clearData.clearPet();
	}
}

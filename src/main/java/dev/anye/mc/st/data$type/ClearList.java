package dev.anye.mc.st.data$type;

import dev.anye.mc.st.config.clear.ClearConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClearList {
	private final List<Entity> list = new ArrayList<>();

	public ClearList() {
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
		int[] count = {0};
		list.forEach(entity -> {
			if (entity.isAlive()) {
				if (ClearConfig.ENTITY_CLEAR.getDatas().safeEntityItem && entity instanceof LivingEntity livingEntity) {
					for (EquipmentSlot slot : EquipmentSlot.values()) {
						ItemStack itemStack = livingEntity.getItemBySlot(slot);
						if (!itemStack.isEmpty()) {
							livingEntity.level().addFreshEntity(new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), itemStack));
						}
						livingEntity.setItemSlot(slot, ItemStack.EMPTY);
					}
				}
				count[0]++;
				entity.discard();
			}
		});
		return count[0];
	}

	public int clearEntityWithCheck() {
		int[] count = {0};
		list.forEach(entity -> {
			if (entity.isAlive() && canClear(entity)) {
				if (ClearConfig.ENTITY_CLEAR.getDatas().safeEntityItem && entity instanceof LivingEntity livingEntity) {

					for (EquipmentSlot slot : EquipmentSlot.values()) {
						ItemStack itemStack = livingEntity.getItemBySlot(slot);
						if (!itemStack.isEmpty()) {
							livingEntity.level().addFreshEntity(new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), itemStack));
						}
						livingEntity.setItemSlot(slot, ItemStack.EMPTY);
					}
                    /*
                    livingEntity.getAllSlots().forEach(itemStack -> {
                        if (!itemStack.isEmpty()) {
                            livingEntity.level().addFreshEntity(new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), itemStack));
                        }
                    });

                     */
				}
				count[0]++;
				entity.discard();
			}
		});
		return count[0];
	}

	public boolean canClear(Entity livingEntity) {
		if (ClearConfig.ENTITY_CLEAR.getDatas().whiteList.contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()))
			return false;
		if (ClearConfig.ENTITY_CLEAR.getDatas().blackList.contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()))
			return true;
		//if (serverLevel.getNearestPlayer(livingEntity, ClearConfig.ENTITY_CLEAR.getDatas().safeDistance) != null) return false;
		if (livingEntity.getCustomName() != null && !ClearConfig.ENTITY_CLEAR.getDatas().clearName) return false;
		if (livingEntity.getType().getCategory().equals(MobCategory.MONSTER) && !ClearConfig.ENTITY_CLEAR.getDatas().clearMob)
			return false;
		else if (livingEntity instanceof Npc && !ClearConfig.ENTITY_CLEAR.getDatas().clearNpc) return false;
		else if (livingEntity instanceof Animal && !ClearConfig.ENTITY_CLEAR.getDatas().clearAnimal) return false;
		else
			return !(livingEntity instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame() || ClearConfig.ENTITY_CLEAR.getDatas().clearPet;
	}
}

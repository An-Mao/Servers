package dev.anye.mc.st.helper;

import dev.anye.mc.st.config.clear.ClearConfig;
import dev.anye.mc.st.config.clear.ItemClearData;
import dev.anye.mc.st.data_type.ClearList;
import dev.anye.mc.st.menu.TrashBinContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClearHelper {
	public static boolean isClearTrashBin = false;

	public static Map<String, String> pullClearCount(int count) {
		return MsgHelper.createMsgMap("$clear.count", String.valueOf(count));
	}


	public static void clearServerEntity(MinecraftServer server) {

		ClearConfig.ENTITY_CLEAR.read(entityClearData -> {
			ClearList clearList = new ClearList(entityClearData);
			server.getAllLevels().forEach(serverLevel -> {
				clearLevelEntity(serverLevel, clearList);
				if (entityClearData.clearXp())
					serverLevel.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), experienceOrb -> true).forEach(clearList::add);
			});
			MsgHelper.sendServerMsg(server, ClearConfig.EntityClear.getMsg(entityClearData,0), pullClearCount(clearList.clearEntity()));
		});
	}

	public static ClearList clearLevelEntity(ServerLevel serverLevel, @NotNull ClearList clearList) {
		if (serverLevel != null) {
			ClearConfig.ENTITY_CLEAR.read(entityClearData -> {
				serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), livingEntity -> true).forEach(livingEntity -> {
					if (!(livingEntity instanceof ServerPlayer)) {
						if (entityClearData.whiteList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()))
							return;
						if (entityClearData.blackList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString())) {
							clearList.add(livingEntity);
							return;
						}
						if (serverLevel.getNearestPlayer(livingEntity, entityClearData.safeDistance()) != null)
							return;
						if (livingEntity.getCustomName() != null && !entityClearData.clearName()) return;
						if (livingEntity.getType().getCategory().equals(MobCategory.MONSTER) && !entityClearData.clearMob()) return;
						else if (livingEntity instanceof Npc && !entityClearData.clearNpc()) return;
						else if (livingEntity instanceof Animal && !entityClearData.clearAnimal()) return;
						else if (livingEntity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame() && !entityClearData.clearPet()) return;
						clearList.add(livingEntity);
						//clears[0]++;
					}
				});
			});
		}
		return clearList;
	}

	public static void clearItem(MinecraftServer server) {
		if (isClearTrashBin) return;
		isClearTrashBin = true;
		TrashBinContainer.close();
		ClearConfig.ITEM_CLEAR.read(itemClearData -> {
			if (itemClearData.clearTrash()) TrashBinContainer.SLOTS.clear();
			int[] clears = {0};
			server.getAllLevels().forEach(serverLevel -> serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemEntity -> true).forEach(entity -> {
				if (itemClearData.whiteList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))
					return;
				if (itemClearData.blackList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString())) {
					clears[0]++;
					clearItem(itemClearData,entity);
					return;
				}
				if (serverLevel.getNearestPlayer(entity, itemClearData.safeDistance()) != null)
					return;
				if (entity.getCustomName() != null && !itemClearData.clearName()) return;
				clears[0]++;
				clearItem(itemClearData,entity);
			}));
			quickSort();
			MsgHelper.sendServerMsg(server, itemClearData.msg().getOrDefault(0, ""), MsgHelper.createMsgMap("$clear.count", String.valueOf(clears[0])));
		});
		isClearTrashBin = false;
	}

	public static void quickSort() {
		HashMap<Integer, ItemStack> map = new HashMap<>();
		List<Integer> checkList = new ArrayList<>();
		TrashBinContainer.SLOTS.forEach((slot, stack) -> {
			if (!checkList.contains(slot)) {
				checkList.add(slot);
				if (!stack.isEmpty()) {
					int[] count = {stack.getCount()};
					TrashBinContainer.SLOTS.forEach((integer, itemStack) -> {
						if (ItemStack.isSameItemSameComponents(itemStack, stack) && !checkList.contains(integer)) {
							checkList.add(integer);
							count[0] += itemStack.getCount();
						}
					});
					while (count[0] > 0) {
						ItemStack is = stack.copy();
						if (count[0] >= is.getMaxStackSize()) {
							count[0] -= is.getMaxStackSize();
							is.setCount(is.getMaxStackSize());
						} else {
							is.setCount(count[0]);
							count[0] = 0;
						}
						map.put(map.size(), is);
					}
				}
			}
		});
		TrashBinContainer.SLOTS.clear();
		TrashBinContainer.SLOTS.putAll(map);
	}

	public static void clearItem(ItemClearData itemClearData, ItemEntity itemEntity) {
		if (itemEntity.isRemoved()) return;
		if (itemClearData.trash())
			TrashBinContainer.SLOTS.put(TrashBinContainer.SLOTS.size(), itemEntity.getItem());
		itemEntity.remove(Entity.RemovalReason.DISCARDED);
	}
}

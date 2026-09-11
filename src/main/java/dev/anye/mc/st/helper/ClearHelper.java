package dev.anye.mc.st.helper;

import dev.anye.mc.st.config.clear.ClearConfig;
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
			serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), livingEntity -> true).forEach(livingEntity -> {
				if (!(livingEntity instanceof ServerPlayer)) {
					if (ClearConfig.ENTITY_CLEAR.getData().whiteList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString()))
						return;
					if (ClearConfig.ENTITY_CLEAR.getData().blackList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString())) {
						clearList.add(livingEntity);
						return;
					}
					if (serverLevel.getNearestPlayer(livingEntity, ClearConfig.ENTITY_CLEAR.getData().safeDistance()) != null)
						return;
					if (livingEntity.getCustomName() != null && !ClearConfig.ENTITY_CLEAR.getData().clearName()) return;
					if (livingEntity.getType().getCategory().equals(MobCategory.MONSTER) && !ClearConfig.ENTITY_CLEAR.getData().clearMob())
						return;
					else if (livingEntity instanceof Npc && !ClearConfig.ENTITY_CLEAR.getData().clearNpc()) return;
					else if (livingEntity instanceof Animal && !ClearConfig.ENTITY_CLEAR.getData().clearAnimal()) return;
					else if (livingEntity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame() && !ClearConfig.ENTITY_CLEAR.getData().clearPet())
						return;
					clearList.add(livingEntity);
					//clears[0]++;
				}
			});
		}
		return clearList;
	}

	public static void clearItem(MinecraftServer server) {
		if (isClearTrashBin) return;
		isClearTrashBin = true;
		if (TrashBinContainer.nowPlayer != null) TrashBinContainer.nowPlayer.closeContainer();
		if (ClearConfig.ITEM_CLEAR.getData().clearTrash()) TrashBinContainer.SLOTS.clear();
		int[] clears = {0};
		server.getAllLevels().forEach(serverLevel -> serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemEntity -> true).forEach(entity -> {
			if (ClearConfig.ITEM_CLEAR.getData().whiteList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))
				return;
			if (ClearConfig.ITEM_CLEAR.getData().blackList().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString())) {
				clears[0]++;
				clearItem(entity);
				return;
			}
			if (serverLevel.getNearestPlayer(entity, ClearConfig.ITEM_CLEAR.getData().safeDistance()) != null)
				return;
			if (entity.getCustomName() != null && !ClearConfig.ITEM_CLEAR.getData().clearName()) return;
			clears[0]++;
			clearItem(entity);
		}));
		quickSort();
		MsgHelper.sendServerMsg(server, ClearConfig.ITEM_CLEAR.getData().msg().getOrDefault(0, ""), MsgHelper.createMsgMap("$clear.count", String.valueOf(clears[0])));
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

	public static void clearItem(ItemEntity itemEntity) {
		if (itemEntity.isRemoved()) return;
		if (ClearConfig.ITEM_CLEAR.getData().trash())
			TrashBinContainer.SLOTS.put(TrashBinContainer.SLOTS.size(), itemEntity.getItem());
		itemEntity.remove(Entity.RemovalReason.DISCARDED);
	}
}

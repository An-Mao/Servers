package dev.anye.mc.st.sys;

import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.slf4j.Logger;

public class EnchantmentExtract {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String FLAG = "isEnchantmentExtract";
	private EnchantmentExtract(){}

	public static boolean insertFlag(final ItemStack itemStack) {
		CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
		CompoundTag tag = customData == null ? new CompoundTag() : customData.copyTag();
		tag.putBoolean(FLAG,true);
		itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
		customData = itemStack.get(DataComponents.CUSTOM_DATA);
		if (customData == null){
			LOGGER.error("(NULL)Can't insert flag to => {}",itemStack);
			return false;
		}else {
			if (customData.contains(FLAG)) return true;
			else {
				LOGGER.error("(FALSE)Can't insert flag to => {}",itemStack);
				return false;
			}
		}
	}

	public static void removeFlag(ItemStack itemStack){
		if (itemStack.has(DataComponents.CUSTOM_DATA)){
			CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
			if (customData != null && customData.contains(FLAG)){
				customData.update(compoundTag -> compoundTag.remove(FLAG));
			}
		}
	}


	public static void onUpdate(AnvilUpdateEvent event, ItemStack left, ItemStack right){
		//EnchantmentExtract.removeFlag(event.getLeft());
		if (!event.getOutput().isEmpty() || !event.getVanillaResult().output().isEmpty()) return;
		if (left.isEmpty() || left.is(Items.ENCHANTED_BOOK) || right.isEmpty() || !right.is(Items.BOOK)) return;
		ItemEnchantments enchantments = left.getTagEnchantments();
		if (enchantments.isEmpty()) {// || !insertFlag(left)
			return;
		}
		ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
		out.set(DataComponents.STORED_ENCHANTMENTS,enchantments);
		event.setOutput(out);
		event.setMaterialCost(1);
		int[] c = {0};
		enchantments.keySet().forEach(enchantmentHolder -> c[0] += enchantmentHolder.value().getAnvilCost() * enchantments.getLevel(enchantmentHolder));
		event.setXpCost(c[0]);
	}



	public static void onTake(ServerPlayer serverPlayer,ItemStack left,ItemStack right){
		if (right.is(Items.BOOK)) {
			CustomData data = left.get(DataComponents.CUSTOM_DATA);
			LOGGER.debug("-------------");
			if (data == null){
				LOGGER.debug("null");
				return;
			}
			CompoundTag tag = data.copyTag();
			LOGGER.debug("tag => {}",tag);
			if (!data.contains(FLAG)) return;
			removeFlag(left);
			left.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
			left.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
			left.set(DataComponents.REPAIR_COST, 0);
			serverPlayer.getInventory().placeItemBackInInventory(left);
		}
	}
}

package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.JsonElement;
import dev.anye.mc.st.helper.ItemHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * 货架物品信息数据记录
 * @param playerUUID 上架的玩家uuid
 * @param count 上架的数量
 * @param price 价格
 * @param data 物品数据
 * @param time 上架时间
 */
public record ShelfItemData (String playerUUID, int count, double price, JsonElement data, long time){
	public ShelfItemData(ServerPlayer serverPlayer, double price, ItemStack stack){
		this(serverPlayer.getStringUUID(),stack.count(),price, ItemHelper.itemToJson(stack,serverPlayer.level().registryAccess()),System.currentTimeMillis());
	}
	public ItemStack getItem(ServerLevel serverLevel){
		return ItemHelper.jsonToItem(data,serverLevel.registryAccess());
	}

	public ShelfItemData copy(int newCount){
		return new ShelfItemData(playerUUID,newCount,price,data,time);
	}
}
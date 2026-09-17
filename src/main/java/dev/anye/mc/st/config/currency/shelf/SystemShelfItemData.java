package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.JsonElement;
import dev.anye.mc.st.helper.ItemHelper;
import net.minecraft.world.item.ItemStack;

import java.math.BigDecimal;

/**
 * 系统物品商店数据记录
 * @param price 价格
 * @param count 数量
 * @param autoReplenishment 自动补货（> 0 时会自动补货，补货数量为上面设置的数量）
 * @param purchaseLimit 购买上限
 * @param cooldown 冷却（> 0 时会自动清除上限）
 */
public record SystemShelfItemData(BigDecimal price, int count, int autoReplenishment, int purchaseLimit, int cooldown, JsonElement data) {


	public ItemStack getItem(){
		return ItemHelper.jsonToItem(data);
	}
}

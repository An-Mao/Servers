package dev.anye.mc.st.sys;

import com.mojang.logging.LogUtils;
import dev.anye.core.time.FastDateTime;
import dev.anye.mc.st.config.currency.BlockCurrency;
import dev.anye.mc.st.config.currency.CurrencyConfig;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.Shelf;
import dev.anye.mc.st.config.currency.entity.EntityCurrencies;
import dev.anye.mc.st.config.currency.entity.EntityCurrencyData;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrency;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrencyData;
import dev.anye.mc.st.config.lang.Language;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Currency {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Currency I = new Currency();
	private final CurrencyConfig config;
	private final BlockCurrency blockCurrency;
	private final EntityCurrencies entityCurrencies;
	private final Shelf shelf;

	public Currency(){
		config = new CurrencyConfig();
		blockCurrency = new BlockCurrency();
		entityCurrencies = new EntityCurrencies();
		shelf = new Shelf();
	}

	public void reload(){
		config.reload();
		blockCurrency.reload();
		entityCurrencies.reload();
		shelf.loadItems();
	}

	public void block(ServerPlayer serverPlayer, Block block){
		if (config.isEnableBlock()){
			double v = blockCurrency.getCurrency(block);
			if (v == 0) v = config.blockDefaultCurrency();
			if (v != 0){
			}
		}
	}
	public void entity(ServerPlayer serverPlayer, LivingEntity entity){
		if (config.isEnableEntity()) {
			EntityCurrencyData data = entityCurrencies.get(entity);
			if (data != null && data.value() > 0 && data.maxValue() > 0) {
				boolean[] r = {false};
				String eid = EntityCurrencies.getEid(entity);
				PlayerEntityCurrency playerEntityCurrency = entityCurrencies.getPlayer(serverPlayer);
				playerEntityCurrency.update(map -> {
					PlayerEntityCurrencyData playerEntityCurrencyData = map.getOrDefault(eid, PlayerEntityCurrencyData.DEFAULT);
					if (playerEntityCurrencyData.getValue().compareTo(BigDecimal.valueOf(data.maxValue())) >= 0) {
						if (System.currentTimeMillis() - playerEntityCurrencyData.getLast() < data.cooldown()) {
							return;
						} else {
							playerEntityCurrencyData.add(data.value());
						}
					} else {
						playerEntityCurrencyData.add(data.value());
					}
					playerEntityCurrencyData.setLast(System.currentTimeMillis());
					map.put(eid, playerEntityCurrencyData);
					r[0] = true;
				});
				if (r[0]){
					playerEntityCurrency.save();
					PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
					playerCurrency.add(data.value(), "kill entity '"+eid+"' get");
				}
			}
		}
	}

	public boolean sell(ServerPlayer serverPlayer,final ItemStack stack,double price){
		if (config.isEnableSellItem()){
			if (shelf.addItem(serverPlayer,stack,price)){
				stack.setCount(0);
				return true;
			}
			return false;
		}
		return false;
	}
	public void sell(ServerPlayer serverPlayer, LivingEntity entity){

	}
	public void sell(ServerPlayer serverPlayer, int xp){

	}

	public void buy(){

	}


	public Shelf shelf(){
		return shelf;
	}

	public List<ItemStack> getShelfItems(){
		FastDateTime fastDateTime = new FastDateTime();
		List<ItemStack> items = new ArrayList<>();
		shelf.getItems().forEach((uuid, data) -> {
			ItemStack itemStack = data.getItem();

			CustomData.update(DataComponents.CUSTOM_DATA,itemStack,compoundTag -> compoundTag.putString("shelf.st.item.uuid",uuid));



			ItemLore itemLore = itemStack.getOrDefault(DataComponents.LORE,ItemLore.EMPTY).withLineAdded(Component.literal(data.price() + " ").append(Language.getComponent(null,"currency.st.name")).withColor(TextColor.GOLD));

			itemLore = itemLore.withLineAdded(Component.literal(fastDateTime.setEpochMillis(data.time()).toDateString("-")).append(" ").append(fastDateTime.toTimeString(":")).withColor(TextColor.BLUE));
			itemStack.set(DataComponents.LORE,itemLore);

			items.add(itemStack);
		});
		return items;
	}
}

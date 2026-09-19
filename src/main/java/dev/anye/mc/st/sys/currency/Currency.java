package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.mc.st.config.currency.BlockCurrency;
import dev.anye.mc.st.config.currency.CurrencyConfig;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.entity.EntityCurrencyData;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrency;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrencyData;
import dev.anye.mc.st.helper.EntityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public final class Currency {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Currency I = new Currency();
	private final CurrencyConfig config;

	private final BlockCurrency blockCurrency;
	private final EntityCurrencyCenter entityCurrencyCenter;


	private final PlayerItemShelf playerItemShelf;
	private final SystemItemShelf systemItemShelf;
	private final PlayerEntityShelf playerEntityShelf;

	public Currency(){
		config = new CurrencyConfig();
		blockCurrency = new BlockCurrency();
		entityCurrencyCenter = new EntityCurrencyCenter();

		playerItemShelf = new PlayerItemShelf();
		systemItemShelf = new SystemItemShelf();
		playerEntityShelf = new PlayerEntityShelf();
	}

	public void reload(){
		config.reload();
		blockCurrency.reload();
		entityCurrencyCenter.reload();
		playerItemShelf.loadItems();
		systemItemShelf.loadSystemItems();

		playerEntityShelf.reload();
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
			EntityCurrencyData data = entityCurrencyCenter.get(entity);
			if (data != null && data.isValid()) {
				String eid = EntityCurrencyCenter.getEid(entity);

				PlayerEntityCurrency playerEntityCurrency = entityCurrencyCenter.getPlayer(serverPlayer, eid);
				PlayerEntityCurrencyData playerEntityCurrencyData = playerEntityCurrency.fetch(d1 -> d1);

				if (playerEntityCurrencyData.getValue().compareTo(data.maxValue()) >= 0
						&& System.currentTimeMillis() - playerEntityCurrencyData.getLast() < data.cooldown()) {
					return;
				}

				playerEntityCurrencyData.add(data.value());
				playerEntityCurrencyData.setLast(System.currentTimeMillis());
				playerEntityCurrency.setData(playerEntityCurrencyData);
				playerEntityCurrency.save();

				PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
				playerCurrency.add(data.value(), "kill entity '" + eid + "' get");
			}
		}
	}

	public boolean sell(ServerPlayer serverPlayer,final ItemStack stack,BigDecimal price){
		if (config.isEnableSellItem()){
			if (playerItemShelf.sell(serverPlayer,stack,price)){
				stack.setCount(0);
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean sellMob(ServerPlayer serverPlayer,BigDecimal price){
		if (config.isEnableSellEntity()) {
			List<Entity> entities = EntityHelper.getPlayerLeash(serverPlayer);
			if (entities.isEmpty()) return false;
			for (Entity entity : entities) {
				playerEntityShelf.sell(serverPlayer,entity,price);
			}
			return true;
		}
		return false;
	}

	public void sell(ServerPlayer serverPlayer, int xp){

	}

	public PlayerItemShelf playerItemShelf(){
		return playerItemShelf;
	}
	public SystemItemShelf systemItemShelf(){
		return systemItemShelf;
	}
	public PlayerEntityShelf playerEntityShelf(){
		return playerEntityShelf;
	}

	public void disconnect(ServerPlayer serverPlayer) {
		entityCurrencyCenter.disconnect(serverPlayer);
	}
}

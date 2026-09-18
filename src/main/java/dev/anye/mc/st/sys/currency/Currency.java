package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.mc.st.config.currency.BlockCurrency;
import dev.anye.mc.st.config.currency.CurrencyConfig;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.entity.EntityCurrencies;
import dev.anye.mc.st.config.currency.entity.EntityCurrencyData;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrency;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrencyData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public final class Currency {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Currency I = new Currency();
	private final CurrencyConfig config;

	private final BlockCurrency blockCurrency;
	private final EntityCurrencies entityCurrencies;


	private final PlayerItemShelf playerItemShelf;
	private final SystemItemShelf systemItemShelf;
	private final PlayerEntityShelf playerEntityShelf;

	public Currency(){
		config = new CurrencyConfig();
		blockCurrency = new BlockCurrency();
		entityCurrencies = new EntityCurrencies();
		playerItemShelf = new PlayerItemShelf();
		systemItemShelf = new SystemItemShelf();
		playerEntityShelf = new PlayerEntityShelf();
	}

	public void reload(){
		config.reload();
		blockCurrency.reload();
		entityCurrencies.reload();
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
			if (playerItemShelf.sell(serverPlayer,stack,price)){
				stack.setCount(0);
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean sellMob(ServerPlayer serverPlayer,double price){
		if (config.isEnableSellEntity()) {
			List<Entity> entities = serverPlayer.level().getEntitiesOfClass(Entity.class, AABB.ofSize(serverPlayer.getBoundingBox().getCenter(), 32, 32, 32), entity -> entity instanceof Leashable leashable && leashable.getLeashHolder() == serverPlayer);
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

}

package dev.anye.mc.st.sys;

import dev.anye.mc.st.config.currency.BlockCurrency;
import dev.anye.mc.st.config.currency.CurrencyConfig;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.Shelf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class Currency {
	public static final Currency I = new Currency();
	private final CurrencyConfig config;
	private final BlockCurrency blockCurrency;
	private final Shelf shelf;

	public Currency(){
		config = new CurrencyConfig();
		blockCurrency = new BlockCurrency();
		shelf = new Shelf();
	}

	public void block(ServerPlayer serverPlayer, Block block){
		if (config.isEnableBlock()){
			double v = blockCurrency.getCurrency(block);
			if (v == 0) v = config.blockDefaultCurrency();
			if (v != 0){
				PlayerCurrency.getPlayerCurrency(serverPlayer).add(v,"block destroy");
			}
		}
	}
	public void entity(ServerPlayer serverPlayer, Entity entity){

	}
	public void time(ServerPlayer serverPlayer){

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

}

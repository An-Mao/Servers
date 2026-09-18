package dev.anye.mc.st.data_type;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IShelf<T> {
	String ID_KEY = "shelf.st.merchandise.uuid";
	default String idKey(){
		return ID_KEY;
	}
	int count();
	List<ItemStack> all(ServerPlayer serverPlayer);
	boolean sell(ServerPlayer serverPlayer, T target, double price);
	boolean buy(ServerPlayer serverPlayer,String key);

	void remove(String key);
}

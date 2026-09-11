package dev.anye.mc.st.helper;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemHelper {
	private ItemHelper(){}

	public static Item getItem(String name) {
		return BuiltInRegistries.ITEM.get(Identifier.parse(name)).map(Holder.Reference::value).orElse(Items.AIR);
	}

	public static Identifier getKey(ItemStack item){
		return getKey(item);
	}

	public static Identifier getKey(Item item){
		return BuiltInRegistries.ITEM.getKey(item);
	}
	public static String getStringKey(ItemStack item){
		return getStringKey(item);
	}

	public static String getStringKey(Item item){
		return getKey(item).toString();
	}

	public static JsonElement itemToJson(ItemStack item){
		return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE,item).getOrThrow();
	}

	public static ItemStack jsonToItem(JsonElement json){
		return ItemStack.CODEC.parse(JsonOps.INSTANCE,json).getOrThrow();
	}
}

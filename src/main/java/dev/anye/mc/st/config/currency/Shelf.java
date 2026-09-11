package dev.anye.mc.st.config.currency;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.ItemHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Shelf {
	private final Map<String,Item.Data> items = new HashMap<>();

	public Shelf(){
		loadItems();
	}


	public void loadItems(){
		_File.getFiles(ConfigDir.SHELF_ITEM,_SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			Shelf.Item item = new Item(_File.getFilePath(ConfigDir.SHELF_ITEM,path.getFileName().toString()));
			item.read(data -> {
					items.put(uuid,data);
			});
		});
	}
	public Map<String,Item.Data> getItems(){
		return items;
	}



	public boolean addItem(ServerPlayer serverPlayer,ItemStack stack,double price){
		UUID uuid = UUID.randomUUID();
		int i = 0;
		while (items.containsKey(uuid.toString())){
			if (i > 99) return false;
			i ++;
			uuid = UUID.randomUUID();
		}
		Item.Data data = new Item.Data(serverPlayer,price,stack);
		new Item(uuid).save(data);
		items.put(uuid.toString(),data);
		return true;
	}

	public static class Item extends _JsonConfigS<Item.Data> {
		public Item(UUID uuid) {
			this(uuid.toString());
		}
		public Item(String uuid) {
			super(_File.getFilePath(ConfigDir.SHELF_ITEM,uuid+_SuffixCDT.JSON_SUFFIX), null, new TypeToken<>(){}, false);
		}


		public record Data(String playerUUID, double price, JsonElement data,long time){
			public Data(ServerPlayer serverPlayer, double price, ItemStack stack){
				this(serverPlayer.getStringUUID(),price,stack);
			}
			public Data(String playerUUID, double price,ItemStack stack){
				this(playerUUID,price, ItemHelper.itemToJson(stack),System.currentTimeMillis());
			}
			public ItemStack getItem(){
				return ItemHelper.jsonToItem(data);
			}
		}

	}
}

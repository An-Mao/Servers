package dev.anye.mc.st.config.currency;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.exception._IOException;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.ItemHelper;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class Shelf {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String,Item.Data> items = new LinkedHashMap<>();
	private final ItemShelfConfig itemConfig = new ItemShelfConfig();

	public Shelf(){
		loadItems();
	}


	public void loadItems(){
		items.clear();
		_File.getFiles(ConfigDir.SHELF_ITEM,_SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			//ConfigDir.SHELF_ITEM,path.getFileName().toString()
			Shelf.Item item = new Item(uuid,null);
			if (item.data() != null){
				items.put(uuid,item.data());
			}
		});
		LOGGER.debug("load shelf item count : {}",items.size());
	}

	public Map<String,Item.Data> getItems(){
		return items;
	}


	public boolean addItem(ServerPlayer serverPlayer,ItemStack stack,double price){
		if (serverPlayer == null) {
			LOGGER.error("server player is null");
			return false;
		}
		if (itemConfig.data() == null) {
			MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.config.error");
			return false;
		}
		if (!itemConfig.data().checkPrice(price)){
			MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.player.error.price");
		}
		double fee = itemConfig.data().getFee(price);
		if (PlayerCurrency.getPlayerCurrency(serverPlayer).sub(fee,"item sell fee")) {
			String uuid = System.currentTimeMillis() + "_" + serverPlayer.getStringUUID();
			String fileTmp = uuid;
			int i = 0;
			while (items.containsKey(fileTmp)) {
				i++;
				fileTmp = uuid + "_" + i;
			}
			Item.Data data = new Item.Data(serverPlayer, price, stack);
			new Item(fileTmp, data);
			items.put(fileTmp, data);
			return true;
		}
		MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.player.error.insufficient_funds");
		return false;
	}

	public boolean buyItem(ServerPlayer serverPlayer,String key){
		if (itemConfig.data() == null) {
			MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.config.error");
			return false;
		}
		if (items.containsKey(key)){
			Item.Data data = items.get(key);
			if (data.count > 0) {
				PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
				String u = data.playerUUID;
				if (playerCurrency.sub(data.price, "buy item '" + key + "' 1")) {
					ItemStack stack = data.getItem();
					stack.setCount(1);
					serverPlayer.getInventory().placeItemBackInInventory(stack);
					int count = data.count - 1;
					if (count < 1) {
						items.remove(key);
						try {
							Files.move(Paths.get(_File.getFilePath(ConfigDir.SHELF_ITEM, key + _SuffixCDT.JSON_SUFFIX)),Paths.get(_File.getFilePath(ConfigDir.SHELF_ITEM_LOG, key + _SuffixCDT.JSON_SUFFIX)));
						} catch (IOException e) {
							throw new _IOException(e);
						}
					}else {
						try {
							Files.delete(Paths.get(_File.getFilePath(ConfigDir.SHELF_ITEM, key + _SuffixCDT.JSON_SUFFIX)));
						} catch (IOException e) {
							throw new _IOException(e);
						}
						new Item(key, data);
						data = data.copy(count);
						items.put(key,data);
					}
					playerCurrency = PlayerCurrency.getPlayerCurrency(u,serverPlayer);
					return playerCurrency.add(itemConfig.data().getTax(data.price),"sell item '" + key + "' 1");
				}else {
					MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.player.error.insufficient_funds");
				}
			}else {
				MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.buy.error.not_have");
			}
		}else {
			MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.buy.error.not_have");
		}
		return false;
	}


































	public static class Item extends _JsonConfigR<Item.Data> {
		public Item(UUID uuid,Data newData) {
			this(uuid.toString(),newData);
		}
		public Item(String uuid,Data newData) {
			super(_File.getFilePath(ConfigDir.SHELF_ITEM,uuid+_SuffixCDT.JSON_SUFFIX), newData, new TypeToken<>(){}, true);
		}


		public record Data(String playerUUID,int count, double price, JsonElement data,long time){
			public Data(ServerPlayer serverPlayer, double price, ItemStack stack){
				this(serverPlayer.getStringUUID(),price,stack);
			}
			public Data(String playerUUID, double price,ItemStack stack){
				this(playerUUID,stack.count(),price, ItemHelper.itemToJson(stack),System.currentTimeMillis());
			}
			public ItemStack getItem(){
				return ItemHelper.jsonToItem(data);
			}

			public Data copy(int newCount){
				return new Data(playerUUID,newCount,price,data,time);
			}
		}

	}
}

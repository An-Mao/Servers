package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.exception._IOException;
import dev.anye.core.system._File;
import dev.anye.core.time.FastDateTime;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.shelf.ShelfItem;
import dev.anye.mc.st.config.currency.shelf.ShelfItemConfig;
import dev.anye.mc.st.config.currency.shelf.ShelfItemData;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.data_type.IShelf;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlayerItemShelf implements IShelf<ItemStack> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, ShelfItemData> items = new LinkedHashMap<>();
	private final ShelfItemConfig itemConfig = new ShelfItemConfig();

	public PlayerItemShelf(){
		loadItems();
	}


	public void loadItems(){
		items.clear();
		_File.getFiles(ConfigDir.CURRENCY_SHELF_ITEM,_SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			//ConfigDir.SHELF_ITEM,path.getFileName().toString()
			ShelfItem item = new ShelfItem(uuid,null);
			if (item.data() != null){
				items.put(uuid,item.data());
			}
		});
		LOGGER.debug("load shelf item count : {}",items.size());
	}

	@Override
	public int count(){
		return items.size();
	}


	@Override
	public List<ItemStack> all(ServerPlayer serverPlayer){
		FastDateTime fastDateTime = new FastDateTime();
		List<ItemStack> playerItems = new ArrayList<>();
		items.forEach((uuid, data) -> {
			ItemStack itemStack = data.getItem(serverPlayer.level());

			CustomData.update(DataComponents.CUSTOM_DATA,itemStack, compoundTag -> compoundTag.putString(idKey(),uuid));


			ItemLore itemLore = itemStack.getOrDefault(DataComponents.LORE,ItemLore.EMPTY).withLineAdded(Component.literal(data.price() + " ").append(Language.getComponent(null,"currency.st.name")).withColor(TextColor.GOLD));

			itemLore = itemLore.withLineAdded(Component.literal(fastDateTime.setEpochMillis(data.time()).toDateString("-")).append(" ").append(fastDateTime.toTimeString(":")).withColor(TextColor.BLUE));
			itemStack.set(DataComponents.LORE,itemLore);

			playerItems.add(itemStack);
		});
		return playerItems;
	}

	public boolean sell(@NotNull ServerPlayer serverPlayer, ItemStack stack, double price){
		if (itemConfigIsLoad()) {
			if (!itemConfig.data().checkPrice(price)) {
				MsgHelper.sendMsgToPlayerF(serverPlayer, "shelf.st.item.player.error.price");
			}
			double fee = itemConfig.data().getFee(price);
			if (PlayerCurrency.getPlayerCurrency(serverPlayer).sub(fee, "item sell fee")) {
				String uuid = System.currentTimeMillis() + "_" + serverPlayer.getStringUUID();
				String fileTmp = uuid;
				int i = 0;
				while (items.containsKey(fileTmp)) {
					i++;
					fileTmp = uuid + "_" + i;
				}
				ShelfItemData data = new ShelfItemData(serverPlayer, price, stack);
				new ShelfItem(fileTmp, data);
				items.put(fileTmp, data);
				return true;
			}
			MsgHelper.sendMsgToPlayerF(serverPlayer, "shelf.st.item.player.error.insufficient_funds");
			return false;
		}
		return false;
	}

	public boolean itemConfigIsLoad(){
		if (itemConfig.data() == null) {
			LOGGER.debug("Item Shelf Config is NULL");
			return false;
		}
		return true;
	}


	public boolean buy(ServerPlayer serverPlayer, String key){
		if (itemConfigIsLoad() && items.containsKey(key)){
			ShelfItemData data = items.get(key);
			if (data.count() > 0) {
				PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
				String u = data.playerUUID();
				if (playerCurrency.sub(data.price(), "buy item '" + key + "' 1")) {
					ItemStack stack = data.getItem(serverPlayer.level());
					stack.setCount(1);
					serverPlayer.getInventory().placeItemBackInInventory(stack);
					int count = data.count() - 1;
					if (count < 1) {
						remove(key);
					}else {
						subItem(key,data,count);
					}
					playerCurrency = PlayerCurrency.getPlayerCurrency(u,serverPlayer);
					return playerCurrency.add(data.price() - itemConfig.data().getTax(data.price()),"sell item '" + key + "' 1");
				}
			}else {
				MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.buy.error.not_have");
			}
		}else {
			MsgHelper.sendMsgToPlayerF(serverPlayer,"shelf.st.item.buy.error.not_have");
		}
		return false;
	}

	@Override
	public void remove(String key){
		items.remove(key);
		try {
			Files.move(Paths.get(_File.getFilePath(ConfigDir.CURRENCY_SHELF_ITEM, key + _SuffixCDT.JSON_SUFFIX)),Paths.get(_File.getFilePath(ConfigDir.CURRENCY_SHELF_ITEM_LOG, key + _SuffixCDT.JSON_SUFFIX)));
		} catch (IOException e) {
			throw new _IOException(e);
		}
	}
	public void subItem(String key,ShelfItemData data,int count){
		try {
			Files.delete(Paths.get(_File.getFilePath(ConfigDir.CURRENCY_SHELF_ITEM, key + _SuffixCDT.JSON_SUFFIX)));
		} catch (IOException e) {
			throw new _IOException(e);
		}
		new ShelfItem(key, data);
		data = data.copy(count);
		items.put(key,data);
	}
}

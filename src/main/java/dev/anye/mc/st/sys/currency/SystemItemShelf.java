package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.shelf.*;
import dev.anye.mc.st.config.lang.Language;
import dev.anye.mc.st.data_type.IShelf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.slf4j.Logger;

import java.util.*;

public final class SystemItemShelf implements IShelf<ItemStack> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, SystemShelfItemData> items = new LinkedHashMap<>();
	/**
	 * 懒加载，仅在调用时加载，后续考虑长时间不使用则释放
	 */
	private final Map<String, SystemShelfItemLog> logs = new HashMap<>();

	public SystemItemShelf(){
		loadSystemItems();
	}


	public SystemShelfItemLog getLog(String itemKey){
		return logs.computeIfAbsent(itemKey, _ -> new SystemShelfItemLog(itemKey));
	}

	@Override
	public int count(){
		return items.size();
	}


	public void loadSystemItems(){
		items.clear();
		_File.getFiles(ConfigDir.CURRENCY_SHELF_SYSTEM_ITEM, _SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			SystemShelfItemIO item = new SystemShelfItemIO(uuid);
			if (item.data() != null){
				items.put(uuid,item.data());
			}
		});
		LOGGER.debug("load system shelf item count : {}", items.size());
	}

	@Override
	public List<ItemStack> all(ServerPlayer serverPlayer){
		List<ItemStack> sysItems = new ArrayList<>();

		items.forEach((uuid, data) -> {
			ItemStack itemStack = data.getItem(serverPlayer.level());

			CustomData.update(DataComponents.CUSTOM_DATA,itemStack, compoundTag -> compoundTag.putString(idKey(),uuid));

			ItemLore itemLore = itemStack.getOrDefault(DataComponents.LORE,ItemLore.EMPTY).withLineAdded(Component.literal(data.price() + " ").append(Language.getComponent(serverPlayer,"currency.st.name")).withColor(TextColor.GOLD));

			itemStack.set(DataComponents.LORE,itemLore);

			sysItems.add(itemStack);
		});
		return sysItems;
	}

	@Override
	public boolean sell(ServerPlayer serverPlayer, ItemStack target, double price) {
		return false;
	}

	@Override
	public boolean buy(ServerPlayer serverPlayer, String itemKey){
		SystemShelfItemData data = items.get(itemKey);
		if (data != null){
			SystemShelfItemLog log = getLog(itemKey);
			SystemShelfItemLogData logData = log.read(data1 -> data1,SystemShelfItemLogData.EMPTY);
			if (logData.count() >= data.count()){
				if (data.autoReplenishment() > 0){
					if (System.currentTimeMillis() - logData.lastTime() < data.autoReplenishment()){
						return false;
					}else {
						logData = new SystemShelfItemLogData(0, System.currentTimeMillis());
					}
				}else return false;
			}
			if (buy(serverPlayer,itemKey,data)){
				SystemShelfItemLogData newData = new SystemShelfItemLogData(logData.count() + 1, logData.lastTime());
				log.setData(newData);
				log.save();
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove(String key) {
	}

	private boolean buy(ServerPlayer serverPlayer, String itemKey, SystemShelfItemData data){
		PlayerSystemShelfItemLog playerSystemShelfItemLog = new PlayerSystemShelfItemLog(serverPlayer,itemKey);
		if (playerSystemShelfItemLog.checkAndAdd(data)){
			PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
			if (playerCurrency.sub(data.price(), "buy item '" + itemKey + "' 1")) {
				ItemStack stack = data.getItem(serverPlayer.level());
				stack.setCount(1);
				serverPlayer.getInventory().placeItemBackInInventory(stack);
				return true;
			}
		}
		return false;
	}
}

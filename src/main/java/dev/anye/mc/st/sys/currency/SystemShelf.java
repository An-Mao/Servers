package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.currency.PlayerCurrency;
import dev.anye.mc.st.config.currency.shelf.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SystemShelf {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, SystemShelfItemData> items = new LinkedHashMap<>();
	/**
	 * 懒加载，仅在调用时加载，后续考虑长时间不使用则释放
	 */
	private final Map<String, SystemShelfItemLog> logs = new HashMap<>();

	public SystemShelf(){
		loadSystemItems();
	}


	public SystemShelfItemLog getLog(String itemKey){
		return logs.computeIfAbsent(itemKey, _ -> new SystemShelfItemLog(itemKey));
	}



	public void loadSystemItems(){
		items.clear();
		_File.getFiles(ConfigDir.SHELF_ITEM, _SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			SystemShelfItemIO item = new SystemShelfItemIO(uuid);
			if (item.data() != null){
				items.put(uuid,item.data());
			}
		});
		LOGGER.debug("load system shelf item count : {}", items.size());
	}

	public void buyItem(ServerPlayer serverPlayer,String itemKey){
		SystemShelfItemData data = items.get(itemKey);
		if (data != null){
			SystemShelfItemLog log = getLog(itemKey);
			SystemShelfItemLogData logData = log.read(data1 -> data1,SystemShelfItemLogData.EMPTY);
			if (logData.count() >= data.count()){
				if (data.autoReplenishment() > 0){
					if (System.currentTimeMillis() - logData.lastTime() < data.autoReplenishment()){
						return;
					}else {
						logData = new SystemShelfItemLogData(logData.count(), System.currentTimeMillis());
					}
				}else return;
			}
			if (buyItem(serverPlayer,itemKey,data)){
				SystemShelfItemLogData newData = new SystemShelfItemLogData(logData.count() + 1, logData.lastTime());
				log.setData(newData);
				log.save();
			}
		}
	}

	public boolean buyItem(ServerPlayer serverPlayer, String itemKey, SystemShelfItemData data){
		PlayerSystemShelfItemLog playerSystemShelfItemLog = new PlayerSystemShelfItemLog(serverPlayer,itemKey);
		if (playerSystemShelfItemLog.checkAndAdd(data)){
			PlayerCurrency playerCurrency = PlayerCurrency.getPlayerCurrency(serverPlayer);
			if (playerCurrency.sub(data.price(), "buy item '" + itemKey + "' 1")) {
				ItemStack stack = data.getItem();
				stack.setCount(1);
				serverPlayer.getInventory().placeItemBackInInventory(stack);
				return true;
			}
		}
		return false;
	}
}

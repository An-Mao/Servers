package dev.anye.mc.st.config.currency.shelf;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import net.minecraft.server.level.ServerPlayer;

/**
 * 玩家商店物品数据记录。
 */
public class PlayerSystemShelfItemLog extends _JsonConfigS<PlayerSystemShelfItemLogData> {
	public PlayerSystemShelfItemLog(String uuid, String itemKey) {
		super(_File.getFilePath(ConfigDir.getPlayerSystemShelfLogDir(uuid),itemKey + _SuffixCDT.JSON_SUFFIX), PlayerSystemShelfItemLogData.EMPTY, new TypeToken<>(){});
	}
	public PlayerSystemShelfItemLog(ServerPlayer serverPlayer, String itemKey) {
		this(serverPlayer.getStringUUID(),itemKey);
	}

	/**
	 * 检查是否已受限制，未受限制则添加，商品的数量检测应在调用前完成
	 * @param systemShelfItemData 系统物品商店物品的配置
	 * @return bool
	 */
	public boolean checkAndAdd(SystemShelfItemData systemShelfItemData){
		PlayerSystemShelfItemLogData oldPlayerData = read(playerSystemShelfItemLogData -> playerSystemShelfItemLogData, PlayerSystemShelfItemLogData.EMPTY);
		if (oldPlayerData != null){
			if (oldPlayerData.count() >= systemShelfItemData.purchaseLimit()){
				if (systemShelfItemData.cooldown() > 0){
					if (System.currentTimeMillis() - oldPlayerData.lastTime() < systemShelfItemData.cooldown()){
						return false;
					}
				}else return false;
			}
			PlayerSystemShelfItemLogData newPlayerData = new PlayerSystemShelfItemLogData(oldPlayerData.count() + 1, System.currentTimeMillis());
			setData(newPlayerData);
			save();
			return true;
		}
		return false;
	}
}

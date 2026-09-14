package dev.anye.mc.st.config.currency.entity;

import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.EntityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class EntityCurrencies {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String,EntityCurrencyData> entities = new HashMap<>();
	private final Map<String,PlayerEntityCurrency> player = new HashMap<>();
	public EntityCurrencies(){
		reload();
	}
	public void reload(){
		loadEntities();
		player.clear();
	}

	public void loadEntities(){
		entities.clear();
		_File.getFiles(ConfigDir.SHELF_ENTITY, _SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			//ConfigDir.SHELF_ITEM,path.getFileName().toString()
			EntityCurrency item = new EntityCurrency(uuid);
			if (item.data() != null){
				entities.put(uuid,item.data());
			}
		});
		LOGGER.debug("load shelf item count : {}", entities.size());
	}

	public PlayerEntityCurrency getPlayer(ServerPlayer serverPlayer){
		return getPlayer(serverPlayer.getStringUUID());
	}
	public PlayerEntityCurrency getPlayer(String uuid){
		return player.computeIfAbsent(uuid, _ -> new PlayerEntityCurrency(uuid));
	}

	public EntityCurrencyData get(LivingEntity entity){
		return get(getEid(entity));
	}
	public EntityCurrencyData get(String entity){
		return entities.computeIfAbsent(entity, _ -> new EntityCurrency(entity).data());
	}
	public static String getEid(LivingEntity entity){
		return EntityHelper.getEntityRegStringIDWithX(entity,"@");
	}
}

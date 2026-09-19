package dev.anye.mc.st.sys.currency;

import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.currency.entity.EntityCurrency;
import dev.anye.mc.st.config.currency.entity.EntityCurrencyData;
import dev.anye.mc.st.config.currency.entity.PlayerEntityCurrency;
import dev.anye.mc.st.helper.EntityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class EntityCurrencyCenter {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, EntityCurrencyData> entities = new HashMap<>();
	private final Map<String,Map<String, PlayerEntityCurrency>> player = new HashMap<>();
	public EntityCurrencyCenter(){
		reload();
	}
	public void reload(){
		loadEntities();
		player.clear();
	}

	public void loadEntities(){
		entities.clear();
		_File.getFiles(ConfigDir.CURRENCY_ENTITY, _SuffixCDT.JSON_SUFFIX).forEach(path -> {
			String uuid = _File.getFileNameWithoutExtension(path.getFileName().toString());
			EntityCurrency entityCurrency = new EntityCurrency(uuid,EntityCurrencyData.DEFAULT);
			if (entityCurrency.data() != null){
				entities.put(uuid,entityCurrency.data());
			}
		});
		LOGGER.debug("load shelf item count : {}", entities.size());
	}

	public PlayerEntityCurrency getPlayer(ServerPlayer serverPlayer,String eid){
		return getPlayer(serverPlayer.getStringUUID(),eid);
	}
	public PlayerEntityCurrency getPlayer(String uuid,String eid){
		return player.computeIfAbsent(uuid, _ -> new HashMap<>()).computeIfAbsent(eid,_ -> new PlayerEntityCurrency(uuid,eid));
	}

	public EntityCurrencyData get(LivingEntity entity){
		return get(getEid(entity));
	}
	public EntityCurrencyData get(String entity){
		return entities.computeIfAbsent(entity, _ -> {
			EntityCurrency entityCurrency = new EntityCurrency(entity,null);
			if (entityCurrency.data() != null) return entityCurrency.data();
			return EntityCurrencyData.DEFAULT;
		});
	}
	public static String getEid(LivingEntity entity){
		return EntityHelper.getEntityRegStringIDWithX(entity,"@");
	}

	public void disconnect(ServerPlayer serverPlayer){
		player.remove(serverPlayer.getStringUUID());
	}
}

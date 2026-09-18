package dev.anye.mc.st.config.currency.entity;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import org.slf4j.Logger;

public class EntityCurrency extends _JsonConfigR<EntityCurrencyData> {
	private static final Logger LOGGER = LogUtils.getLogger();

	public EntityCurrency(String entity) {
		super(path(entity), EntityCurrencyData.DEFAULT, new TypeToken<>(){},false);
	}


	private static String path(String entity) {
		return _File.getFilePath(ConfigDir.CURRENCY_ENTITY, entity + _SuffixCDT.JSON_SUFFIX);
	}
}
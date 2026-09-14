package dev.anye.mc.st.config.currency.entity;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.HashMap;
import java.util.Map;

public class PlayerEntityCurrency extends _JsonConfigS<Map<String,PlayerEntityCurrencyData>> {
	public PlayerEntityCurrency(String uuid) {
		super(_File.getFilePath(ConfigDir.getPlayerCurrencyDir(uuid),"entities"+ _SuffixCDT.JSON_SUFFIX), new HashMap<>(), new TypeToken<>(){});
	}
}

package dev.anye.mc.st.config.currency.entity;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.HashMap;
import java.util.Map;

public class PlayerEntityCurrency extends _JsonConfigS<PlayerEntityCurrencyData> {
	public PlayerEntityCurrency(String uuid,String eid) {
		super(_File.getFilePath(ConfigDir.getPlayerCurrencyEntityDir(uuid),eid + _SuffixCDT.JSON_SUFFIX),PlayerEntityCurrencyData.DEFAULT, new TypeToken<>(){});
	}
}

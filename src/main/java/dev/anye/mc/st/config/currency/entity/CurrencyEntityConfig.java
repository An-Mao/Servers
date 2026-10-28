package dev.anye.mc.st.config.currency.entity;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigR;

public class CurrencyEntityConfig extends _JsonConfigR<CurrencyEntityConfigData> {
	protected CurrencyEntityConfig(String filePath, CurrencyEntityConfigData defaultData, TypeToken<CurrencyEntityConfigData> typeToken) {
		super(filePath, defaultData, typeToken);
	}
}

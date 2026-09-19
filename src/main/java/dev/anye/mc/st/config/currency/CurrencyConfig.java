package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

public class CurrencyConfig extends _JsonConfigS<CurrencyConfigData> {
	public static final String FILE = _File.getFilePath(ConfigDir.BASE,"currency.json");


	public CurrencyConfig() {
		super(FILE, new CurrencyConfigData(), new TypeToken<>(){});
	}


	public double blockDefaultCurrency(){
		return fetch(CurrencyConfigData::blockDefaultCurrency,0D);
	}

	public boolean isEnable(){
		return fetch(CurrencyConfigData::isEnable,false);
	}

	public boolean isEnableBlock(){
		return fetch(CurrencyConfigData::isEnableBlock,false);
	}
	public boolean isEnableEntity(){
		return fetch(CurrencyConfigData::isEnableEntity,false);
	}

	public boolean isEnableSellItem(){
		return fetch(CurrencyConfigData::isEnableSellItem,false);
	}
	public boolean isEnableSellEntity(){
		return fetch(CurrencyConfigData::isEnableSellEntity,false);
	}
	public boolean isEnableSellXp(){
		return fetch(CurrencyConfigData::isEnableSellXp,false);
	}


}

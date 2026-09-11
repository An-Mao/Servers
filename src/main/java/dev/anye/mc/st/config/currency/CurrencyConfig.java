package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.function.Consumer;

public class CurrencyConfig extends _JsonConfigS<CurrencyConfig.Data> {
	public static final String FILE = _File.getFilePath(ConfigDir.BASE,"currency.json");
	public CurrencyConfig() {
		super(FILE, new Data(), new TypeToken<>(){});
	}


	public double blockDefaultCurrency(){
		return read(data1 -> data1.blockDefaultCurrency,0D);
	}

	public boolean isEnableBlock(){
		return read(data1 -> data1.enable && data1.block,false);
	}
	public boolean isEnableEntity(){
		return read(data1 -> data1.enable && data1.entity,false);
	}

	public boolean isEnableSellItem(){
		return read(data1 -> data1.enable && data1.sellItem,false);
	}
	public boolean isEnableSellEntity(){
		return read(data1 -> data1.enable && data1.sellEntity,false);
	}
	public boolean isEnableSellXp(){
		return read(data1 -> data1.enable && data1.sellXp,false);
	}


	public boolean isEnable(){
		return read(value -> value.enable,false);
	}



	public static class Data{
		private boolean enable = true;

		private boolean block = true;
		private boolean entity = true;
		private double blockDefaultCurrency = 0;
		private double entityDefaultCurrency = 0;

		private boolean sellItem = true;
		private boolean sellEntity = true;
		private boolean sellXp = true;


	}
}

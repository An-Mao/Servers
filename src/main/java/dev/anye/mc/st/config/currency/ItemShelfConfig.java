package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfigR;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

public class ItemShelfConfig extends _JsonConfigR<ItemShelfConfig.Data> {
	public static final String FILE = _File.getFilePath(ConfigDir.BASE,"ItemShelf.json");

	protected ItemShelfConfig() {
		super(FILE, Data.DEFAULT, new TypeToken<>(){});
	}

	public record Data(
			boolean enable,
			double fee, boolean percentageFee,
			double tax, boolean percentageTax,
			double minPrice, double maxPrice
	){
		public static final Data DEFAULT = new Data(true,10,false,0.1,true,0,0);

		public double getFee(double price){
			return percentageFee ? price * fee : fee;
		}
		public double getTax(double price){
			return percentageTax ? price * tax : tax;
		}
		public boolean checkPrice(double price){
			return price > minPrice && price < (maxPrice == 0 ? Double.MAX_VALUE : maxPrice);
		}
	}
}

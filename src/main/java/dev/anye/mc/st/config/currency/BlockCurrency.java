package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.ItemHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BlockCurrency extends _JsonConfigS<Map<String,Double>> {
	private static final String FILE = _File.getFilePath(ConfigDir.CURRENCY,"block.json");
	public BlockCurrency() {
		super(FILE, Default(), new TypeToken<>(){});
	}
	public static Map<String,Double> Default(){
		Map<String,Double> map = new HashMap<>();
		map.put(ItemHelper.getStringKey(Items.OBSIDIAN),0.2D);
		return map;
	}

	public double getCurrency(Block block){
		return read(map -> map.getOrDefault(block.getDescriptionId(),0D),0D);
	}
}

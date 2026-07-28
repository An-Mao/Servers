package dev.anye.mc.st;

import com.mojang.logging.LogUtils;
import dev.anye.core.pack._Pack;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.Language;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ST.MOD_ID)
public class ST {
	public static final String MOD_ID = "st";
	private static final Logger LOGGER = LogUtils.getLogger();

	static {
		_Pack.writeFiles("assets/st/lang/", ConfigDir.LANGUAGE, ".json","en_us", "zh_cn");
		_Pack.writeFiles("assets/st/js/", ConfigDir.JAVASCRIPT, ".js", "TpaRequest");

		Language.loadLanguage();
	}

	public ST(IEventBus modEventBus, ModContainer modContainer) {
	}
}

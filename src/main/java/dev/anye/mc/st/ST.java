package dev.anye.mc.st;

import dev.anye.core.pack._Pack;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.listen.ListenHandleRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ST.MOD_ID)
public class ST {
	public static final String MOD_ID = "st";

	static {
		_Pack.writeFiles("assets/st/lang/", ConfigDir.LANGUAGE, ".json", "zh_cn");
		_Pack.writeFiles("assets/st/js/", ConfigDir.JAVASCRIPT, ".js", "TpaRequest");
	}

	public ST(IEventBus modEventBus, ModContainer modContainer) {
		ListenHandleRegister.register(modEventBus);
	}
}

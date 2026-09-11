package dev.anye.mc.st;

import com.mojang.logging.LogUtils;
import dev.anye.core.pack._Pack;
import dev.anye.core.system.task.TimingWheel;
import dev.anye.core.time.FastDateTime;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.Language;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Mod(ST.MOD_ID)
public class ST {
	public static final String MOD_ID = "st";
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final FastDateTime FAST_DATE_TIME = new FastDateTime();
	public static final TimingWheel TIMING_WHEEL = TimingWheel.builder()
			.tick(10, TimeUnit.MILLISECONDS)
			.wheelSize(512)
			.executor(ForkJoinPool.commonPool())
			.threadName("ST-Timer")
			.daemon(true)
			.build();

	static {
		_Pack.writeFiles("assets/st/lang/", ConfigDir.LANGUAGE, ".json","en_us", "zh_cn");
		_Pack.writeFiles("assets/st/js/", ConfigDir.JAVASCRIPT, ".js", "TpaRequest");

		Language.loadLanguage();
	}

	public ST(IEventBus modEventBus, ModContainer modContainer) {
	}
}

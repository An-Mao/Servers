package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public final class PlayerCurrency extends _JsonConfigS<PlayerCurrency.Data> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ServerPlayer serverPlayer;

	public PlayerCurrency(ServerPlayer serverPlayer) {
		super(path(serverPlayer.getStringUUID()), new Data(), new TypeToken<>(){}, false);
		this.serverPlayer = serverPlayer;
	}

	public static String path(String uuid){
		return _File.getFilePath(ConfigDir.getPlayerCurrencyDir(uuid),"currency.json");
	}

	public PlayerCurrencyLog getLog(){
		return new PlayerCurrencyLog(serverPlayer);
	}

	public static String getSource(){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String className = "UnknownClass";
		String methodName = "UnknownMethod";
		if (stackTraceElements.length >= 3) {
			className = stackTraceElements[2].getClassName();
			methodName = stackTraceElements[2].getMethodName();
		}
		return className + "$==>" + methodName;
	}

	public boolean add(double value,String source){
		if (source.isEmpty()) return false;
		if (value < 0) return sub(-value,source);
		return read(playerCurrencyData -> {
			if (playerCurrencyData.isLock()) return false;
			if (playerCurrencyData.currency() + value < 0) return false;
			if (getLog().writeLog(source,value)) {
				playerCurrencyData.add(value);
				return true;
			}
			return false;

		},false);
	}

	public boolean sub(double value,String source){
		if (source.isEmpty()) return false;
		if (value < 0) return add(-value,source);
		return read(playerCurrencyData -> {
			if (playerCurrencyData.isLock()) return false;
			if (playerCurrencyData.currency() - value < 0) return false;
			if (getLog().writeLog(source,value)) {
				playerCurrencyData.add(value);
				return true;
			}
			return false;
		},false);
	}


	public static PlayerCurrency getPlayerCurrency(ServerPlayer serverPlayer){
		return new PlayerCurrency(serverPlayer);
	}






	public static final class Data{
		private double currency;
		private boolean lock;
		public Data(){
			this(0,false);
		}
		public Data(double currency,boolean lock){
			this.currency = currency;
			this.lock = lock;
		}

		public void lock(){
			this.lock = true;
		}
		public void unlock(){
			this.lock = false;
		}
		public boolean isLock(){
			return lock;
		}

		public double currency(){
			return currency;
		}
		private void add(double value){
			currency += value;
		}

	}

}

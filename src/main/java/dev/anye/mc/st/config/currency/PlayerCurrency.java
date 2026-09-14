package dev.anye.mc.st.config.currency;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.MsgHelper;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class PlayerCurrency extends _JsonConfigS<PlayerCurrency.Data> {
	private static final Logger LOGGER = LogUtils.getLogger();


	private static final Map<String,PlayerCurrency> PLAYER_CURRENCY_MAP = new HashMap<>();
	private final ServerPlayer off;
	private final String uuid;

	public PlayerCurrency(ServerPlayer main,ServerPlayer off) {
		this(main.getStringUUID(), off);
	}
	public PlayerCurrency(String uuid,ServerPlayer off) {
		super(path(uuid), new Data(), new TypeToken<>(){}, false);
		this.off = off;
		this.uuid = uuid;
	}

	public static String path(String uuid){
		return _File.getFilePath(ConfigDir.getPlayerCurrencyDir(uuid),"currency.json");
	}
/*
	public PlayerCurrencyLog getLog(){
		return new PlayerCurrencyLog(serverPlayer);
	}*/

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
		if (Boolean.TRUE.equals(read(playerCurrencyData -> {
			if (playerCurrencyData.isLock()) {
				MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.locked");
				return false;
			}
			/*if (playerCurrencyData.currency() > 0 && playerCurrencyData.currency() + value < 0) {
				MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.currency_overrun");
				return false;
			}*/
			if (PlayerCurrencyLog.writeLog(uuid,source+"=>+"+value)) {
				playerCurrencyData.add(value);
				return true;
			}
			MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.write_log");
			return false;
		}))){
			save();
			return true;
		}
		return false;
	}

	public boolean sub(double value,String source){
		if (source.isEmpty()) return false;
		if (value < 0) return add(-value,source);
		if (Boolean.TRUE.equals(read(playerCurrencyData -> {
			if (playerCurrencyData.isLock()) {
				MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.locked");
				return false;
			}
			BigDecimal decimal = BigDecimal.valueOf(value);
			if (!playerCurrencyData.check(decimal)) {
				MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.insufficient_funds");
				return false;
			}
			if (PlayerCurrencyLog.writeLog(uuid,source+"=>-"+value)) {
				playerCurrencyData.sub(value);
				return true;
			}
			MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.write_log");
			return false;
		}))){
			save();
			return true;
		}
		MsgHelper.sendMsgToPlayerF(off,"currency.st.player.config.error");
		return false;
	}


	public static PlayerCurrency getPlayerCurrency(ServerPlayer serverPlayer){
		return getPlayerCurrency(serverPlayer.getStringUUID(),serverPlayer);
	}

	public static PlayerCurrency getPlayerCurrency(String uuid,ServerPlayer serverPlayer){
		return PLAYER_CURRENCY_MAP.computeIfAbsent(uuid, _ -> new PlayerCurrency(uuid,serverPlayer));
	}






	public static final class Data{
		private BigDecimal currency;
		private boolean lock;
		public Data(){
			this("0",false);
		}
		public Data(String currency,boolean lock){
			this.currency = new BigDecimal(currency);
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

		public BigDecimal currency(){
			return currency;
		}
		public boolean check(){
			return check(BigDecimal.ZERO);
		}

		public boolean check(double v){
			return check(BigDecimal.valueOf(v));
		}

		/**
		 * 判断当前值与给定值的大小
		 * @param v 给定值
		 * @return 如果小于则返回false，大于或等于返回true
		 */
		public boolean check(BigDecimal v){
			return currency.compareTo(v) >= 0;
		}

		private void add(double value){
			add(BigDecimal.valueOf(value));
		}

		private void add(BigDecimal value){
			currency = currency.add(value);
		}
		private void sub(double value){
			sub(BigDecimal.valueOf(value));
		}
		private void sub(BigDecimal value){
			currency = currency.subtract(value);
		}

	}

}

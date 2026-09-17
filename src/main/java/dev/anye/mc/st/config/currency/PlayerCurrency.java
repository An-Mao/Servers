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

public final class PlayerCurrency extends _JsonConfigS<PlayerCurrencyData> {
	private static final Logger LOGGER = LogUtils.getLogger();


	private static final Map<String,PlayerCurrency> PLAYER_CURRENCY_MAP = new HashMap<>();
	private final ServerPlayer off;
	private final String uuid;

	public PlayerCurrency(ServerPlayer main,ServerPlayer off) {
		this(main.getStringUUID(), off);
	}
	public PlayerCurrency(String uuid,ServerPlayer off) {
		super(path(uuid), new PlayerCurrencyData(), new TypeToken<>(){}, false);
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



	public boolean add(BigDecimal value,String source){
		if (source.isEmpty()) return false;
		if (value.compareTo(BigDecimal.ZERO) < 0) return sub(value.negate(),source);
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


	public boolean add(double value,String source){
		return add(BigDecimal.valueOf(value),source);
	}

	public boolean sub(BigDecimal value,String source){
		if (source.isEmpty()) return false;
		if (value.compareTo(BigDecimal.ZERO) < 0) return add(value.negate(),source);
		if (Boolean.TRUE.equals(read(playerCurrencyData -> {
			if (playerCurrencyData.isLock()) {
				MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.locked");
				return false;
			}
			if (!playerCurrencyData.check(value)) {
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


	public boolean sub(double value,String source){
		return sub(BigDecimal.valueOf(value),source);
	}


	public static PlayerCurrency getPlayerCurrency(ServerPlayer serverPlayer){
		return getPlayerCurrency(serverPlayer.getStringUUID(),serverPlayer);
	}

	public static PlayerCurrency getPlayerCurrency(String uuid,ServerPlayer serverPlayer){
		return PLAYER_CURRENCY_MAP.computeIfAbsent(uuid, _ -> new PlayerCurrency(uuid,serverPlayer));
	}

}

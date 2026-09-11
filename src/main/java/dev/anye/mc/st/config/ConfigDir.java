package dev.anye.mc.st.config;

import com.mojang.logging.LogUtils;
import dev.anye.core.exception._IOException;
import dev.anye.core.system._File;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class ConfigDir {
	public static final String PLAYER_CURRENCY = "Currency";
	private static final Logger LOGGER = LogUtils.getLogger();


	public static final String BASE = _File.getFileFullPathWithRun("ServerTools");
	public static final String LANGUAGE = _File.getFilePath(BASE, "Lang");
	public static final String MSG = _File.getFilePath(BASE, "Msg");
	public static final String CLEAR = _File.getFilePath(BASE, "Clear");
	public static final String PLAYER_GROUP = _File.getFilePath(BASE, "PlayerGroup");
	public static final String BLACK_LIST = _File.getFilePath(BASE, "BlackList");
	public static final String JAVASCRIPT = _File.getFilePath(BASE, "JavaScript");
	public static final String LOGIN = _File.getFilePath(BASE, "Login");
	public static final String DATA = _File.getFilePath(BASE, "Data");
	public static final String PLAYER = _File.getFilePath(BASE, "Player");
	public static final String PLAYER_DATA = _File.getFilePath(DATA, "Player");
	public static final String LISTEN_HANDLE = _File.getFilePath(BASE, "ListenHandle");

	public static final String CURRENCY = _File.getFilePath(BASE, "Currency");
	public static final String CURRENCY_SHELF = _File.getFilePath(CURRENCY, "Shelf");
	public static final String SHELF_ITEM = _File.getFilePath(CURRENCY_SHELF, "Item");
	public static final String SHELF_ENTITY = _File.getFilePath(CURRENCY_SHELF, "Entity");
	public static final String SHELF_XP = _File.getFilePath(CURRENCY_SHELF, "Xp");






	static {
		_File.checkAndCreateDir(BASE);
		_File.checkAndCreateDir(LANGUAGE);
		_File.checkAndCreateDir(MSG);
		_File.checkAndCreateDir(CLEAR);
		_File.checkAndCreateDir(PLAYER_GROUP);
		_File.checkAndCreateDir(BLACK_LIST);
		_File.checkAndCreateDir(JAVASCRIPT);
		_File.checkAndCreateDir(LOGIN);
		_File.checkAndCreateDir(DATA);
		_File.checkAndCreateDir(PLAYER);
		_File.checkAndCreateDir(PLAYER_DATA);
		_File.checkAndCreateDir(CURRENCY);
		_File.checkAndCreateDir(CURRENCY_SHELF);
		_File.checkAndCreateDir(SHELF_ITEM);
		_File.checkAndCreateDir(SHELF_ENTITY);
		_File.checkAndCreateDir(SHELF_XP);
	}


	public static String getPlayerDir(ServerPlayer serverPlayer){
		return getPlayerDir(serverPlayer.getStringUUID());
	}
	public static String getPlayerDir(String uuid){
		String dir = _File.getFilePath(PLAYER,uuid);
		if (_File.checkAndCreateDir(dir)){
			return dir;
		}
		throw new _IOException(dir);
	}
	public static String getPlayerCurrencyDir(ServerPlayer serverPlayer){
		return getPlayerCurrencyDir(serverPlayer.getStringUUID());
	}
	public static String getPlayerCurrencyDir(String uuid){
		String dir = _File.getFilePath(PLAYER,uuid, PLAYER_CURRENCY);
		if (_File.checkAndCreateDir(dir)){
			return dir;
		}
		throw new _IOException(dir);
	}


}

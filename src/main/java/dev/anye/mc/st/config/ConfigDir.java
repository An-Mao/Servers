package dev.anye.mc.st.config;

import com.mojang.logging.LogUtils;
import dev.anye.core.exception._IOException;
import dev.anye.core.system._File;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class ConfigDir {
	private static final String CURRENCY_KEY = "Currency";
	private static final String ENTITY_KEY = "Entity";
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final String BASE = _File.getFileFullPathWithRun("ServerTools");

	public static final String LANGUAGE = _File.getFilePath(BASE, "Lang");
	public static final String MSG = _File.getFilePath(BASE, "Msg");
	public static final String CLEAR = _File.getFilePath(BASE, "Clear");
	public static final String PLAYER_GROUP = _File.getFilePath(BASE, "PlayerGroup");
	public static final String BLACK_LIST = _File.getFilePath(BASE, "BlackList");
	public static final String JAVASCRIPT = _File.getFilePath(BASE, "JavaScript");
	public static final String LOGIN = _File.getFilePath(BASE, "Login");
	public static final String PLAYER = _File.getFilePath(BASE, "Player");

	public static final String DATA = _File.getFilePath(BASE, "Data");
	public static final String LISTEN_HANDLE = _File.getFilePath(BASE, "ListenHandle");

	//------------------------------------Currency----------------------------------
	public static final String CURRENCY = _File.getFilePath(BASE, CURRENCY_KEY);


	public static final String CURRENCY_SHELF = _File.getFilePath(CURRENCY, "Shelf");
	public static final String CURRENCY_SHELF_LOG = _File.getFilePath(CURRENCY_SHELF, "Log");
	public static final String CURRENCY_SHELF_SYSTEM_ITEM = _File.getFilePath(CURRENCY_SHELF, "SystemItem");
	public static final String CURRENCY_SHELF_SYSTEM_ITEM_LOG = _File.getFilePath(CURRENCY_SHELF_LOG, "SystemItem");
	public static final String CURRENCY_SHELF_ITEM = _File.getFilePath(CURRENCY_SHELF, "Item");
	public static final String CURRENCY_SHELF_ITEM_LOG = _File.getFilePath(CURRENCY_SHELF_LOG, "Item");
	public static final String CURRENCY_SHELF_ENTITY = _File.getFilePath(CURRENCY_SHELF, ENTITY_KEY);
	public static final String CURRENCY_SHELF_ENTITY_LOG = _File.getFilePath(CURRENCY_SHELF_LOG, ENTITY_KEY);

	public static final String CURRENCY_ENTITY = _File.getFilePath(CURRENCY, ENTITY_KEY);





	public static final String SHELF_XP = _File.getFilePath(CURRENCY_SHELF, "Xp");

	private static final Map<String,String> playerDirTmp = new HashMap<>();





	static {
		LOGGER.debug("create dir start");
		_File.checkAndCreateDir(BASE);

		_File.checkAndCreateDir(LANGUAGE);
		_File.checkAndCreateDir(MSG);

		_File.checkAndCreateDir(CLEAR);
		_File.checkAndCreateDir(PLAYER_GROUP);
		_File.checkAndCreateDir(BLACK_LIST);
		_File.checkAndCreateDir(JAVASCRIPT);
		_File.checkAndCreateDir(LOGIN);

		_File.checkAndCreateDir(PLAYER);


		_File.checkAndCreateDir(CURRENCY);

		_File.checkAndCreateDir(CURRENCY_SHELF);
		_File.checkAndCreateDir(CURRENCY_SHELF_LOG);
		_File.checkAndCreateDir(CURRENCY_SHELF_ITEM);
		_File.checkAndCreateDir(CURRENCY_SHELF_ITEM_LOG);
		_File.checkAndCreateDir(CURRENCY_SHELF_SYSTEM_ITEM);
		_File.checkAndCreateDir(CURRENCY_SHELF_SYSTEM_ITEM_LOG);
		_File.checkAndCreateDir(CURRENCY_SHELF_ENTITY);
		_File.checkAndCreateDir(CURRENCY_SHELF_ENTITY_LOG);

		_File.checkAndCreateDir(CURRENCY_ENTITY);

		LOGGER.debug("create dir done");
	}
	private ConfigDir(){}

	public static String getPlayerCurrencyDir(String uuid){
		return getPlayerDataDirWith(uuid, CURRENCY_KEY);
	}

	/**
	 * 获取玩家数据目录，无法创建时将抛出IO错误
	 * @return 已创建好的目录
	 */
	public static String getPlayerDataDir(String uuid){
		return playerDirTmp.computeIfAbsent(uuid, _ -> {
			String dir = _File.getFilePath(PLAYER,uuid);
			if (_File.checkAndCreateDir(dir)){
				return dir;
			}
			throw new _IOException(dir);
		});
	}

	/**
	 * 从玩家数据目录取回指定目录路径，无法创建时将抛出IO错误
	 * @return 已创建好的目录
	 */
	public static String getPlayerDataDirWith(String uuid,String path){
		return playerDirTmp.computeIfAbsent(uuid +":"+ path, _ -> {
			String dir = _File.getFilePath(PLAYER,uuid,path);
			if (_File.checkAndCreateDir(dir)){
				return dir;
			}
			throw new _IOException(dir);
		});
	}

	public static String getPlayerSystemShelfLogDir(String uuid){
		return getPlayerDataDirWith(uuid,_File.getFilePath("Log","SystemShelf"));
	}

	public static String getPlayerCurrencyEntityDir(String uuid){
		return getPlayerDataDirWith(uuid,_File.getFilePath(CURRENCY_KEY,ENTITY_KEY));
	}

}

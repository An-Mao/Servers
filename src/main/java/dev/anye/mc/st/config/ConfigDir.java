package dev.anye.mc.st.config;

import dev.anye.core.pack._Pack;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;

public class ConfigDir {
	public static final String BASE = _File.getFileFullPathWithRun("ServerTools");
	public static final String LANGUAGE = _File.getFilePath(BASE , "Lang");
	public static final String MSG = _File.getFilePath(BASE , "Msg");
	public static final String CLEAR = _File.getFilePath(BASE , "Clear");
	public static final String PLAYER_GROUP = _File.getFilePath(BASE , "PlayerGroup");
	public static final String BLACK_LIST = _File.getFilePath(BASE , "BlackList");
	public static final String JAVASCRIPT = _File.getFilePath(BASE , "JavaScript");
	public static final String LOGIN = _File.getFilePath(BASE , "Login");
	public static final String DATA = _File.getFilePath(BASE , "Data");
	public static final String PLAYER_DATA = _File.getFilePath(DATA , "Player");
	public static final String LISTEN_HANDLE = _File.getFilePath(BASE , "ListenHandle");
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
		_File.checkAndCreateDir(PLAYER_DATA);
		_File.checkAndCreateDir(LISTEN_HANDLE);
	}
}

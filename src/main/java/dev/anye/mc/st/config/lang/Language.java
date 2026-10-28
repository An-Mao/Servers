package dev.anye.mc.st.config.lang;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfigX;
import dev.anye.core.json._JsonSupport;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.Config;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 消息本地化类
 */
public class Language extends _JsonConfigX<Map<String, String>> {
	private static final String DEFAULT_LANG_KEY = "en_us";
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Language DEFAULT_LANG =
			new Language("default.json",
					_JsonSupport.readByPack("assets/st/lang/en_us.json", new TypeToken<>() {}),
					true
			);
	private static final Map<String,Language> LANG = new HashMap<>();


	public Language(String filePath,boolean r) {
		this(filePath, new HashMap<>(),r);
	}
	public Language(String filePath,Map<String, String> data,boolean r) {
		super(getFilePath(filePath), data, new TypeToken<>() {},r);
	}

	public static String getFilePath(String file){
		return _File.getFilePath(ConfigDir.LANGUAGE, file);
	}

	public static void reloadLanguage(){
		LANG.clear();
		loadLanguage();
	}
	public static void loadLanguage(){
		_File.getFiles(ConfigDir.LANGUAGE, _SuffixCDT.JSON_SUFFIX).forEach(path -> LANG.put(path.getFileName().toString(),new Language(path.getFileName().toString(),false)));
	}



	public static String getLanguage() {
		String lang = Config.I.fetch(configData -> configData.lang,"");
		if (lang.isBlank() || lang.equals("auto")) {
			Locale defaultLocale = Locale.getDefault();
			lang = defaultLocale.getLanguage() + "_" + defaultLocale.getCountry();
		}
		lang = lang.toLowerCase();
		File file = new File(_File.getFilePath(ConfigDir.LANGUAGE, lang + ".json"));
		if (!file.exists()) {
			return DEFAULT_LANG_KEY;
		}
		return file.getName();
	}

	public static String translatable(String key,Object... value) {
		return translatable(DEFAULT_LANG_KEY, key,value);
	}
	public static String translatable(ServerPlayer serverPlayer,String key,Object... value) {
		if (serverPlayer == null) return translatable("",key, key);
		return translatable(PlayerConfig.get(serverPlayer).lang(),key, key,value);
	}
	public static String translatable(String lang,String key,Object... value) {
		return translatable(lang,key, key,value);
	}

	public static String translatable(String lang,String key,String def,Object... value){
		if (lang.isBlank()) lang = DEFAULT_LANG_KEY;
		lang = lang + _SuffixCDT.JSON_SUFFIX;
		Language language = hasLanguage(lang) ? LANG.get(lang) : DEFAULT_LANG;
		String msg = language.data().getOrDefault(key,def);
		return MessageFormat.format(msg,value);
	}


	public static Component getComponent(ServerPlayer serverPlayer, String key) {
		return Component.literal(translatable(serverPlayer,key));
	}

	public static boolean hasLanguage(String lang){
		if (containsLanguage(lang)){
			return true;
		}
		if (new File(getFilePath(lang)).exists()){
			Language language = new Language(lang,false);
			if (language.data() != null){
				LANG.put(lang,language);
				return true;
			}
		}
		return false;
	}

	public static boolean containsLanguage(String lang){
		return LANG.containsKey(lang);
	}
}

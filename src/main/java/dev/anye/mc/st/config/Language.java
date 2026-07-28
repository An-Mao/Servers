package dev.anye.mc.st.config;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.cdt._SuffixCDT;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.player_data.PlayerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Language extends _JsonConfig<Map<String, String>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String FILE = _File.getFilePath(ConfigDir.LANGUAGE,"default.json");
	private static final Language DEFAULT_LANG = new Language("default.json",Default(),true);
	public static final Map<String,Language> LANG = new HashMap<>();

	public Language(String filePath,boolean r) {
		this(filePath, new HashMap<>(),r);
	}
	public Language(String filePath,Map<String, String> data,boolean r) {
		super(_File.getFilePath(ConfigDir.LANGUAGE, filePath), data, new TypeToken<>() {},r);
	}

	public static void reloadLanguage(){
		LANG.clear();
		loadLanguage();
	}
	public static void loadLanguage(){
		_File.getFiles(ConfigDir.LANGUAGE, _SuffixCDT.JSON_SUFFIX).forEach(path -> LANG.put(path.getFileName().toString(),new Language(path.getFileName().toString(),false)));
	}

	public static Map<String, String> Default() {
		Map<String, String> map = new HashMap<>();
		map.put("command.tpa_deny.failed", "Tpa deny failed");
		map.put("command.tpa_deny.success", "Tpa deny successfully");
		map.put("command.tpa_accept.failed", "Tpa accept failed");
		map.put("command.tpa_accept.success", "Tpa accept successfully");
		map.put("command.tpa.failed", "Tpa failed");
		map.put("command.tpa.success", "Tpa successfully");
		map.put("command.tpa.msg", "TpaRequest.js");
		map.put("command.tpa.msg.tip", " sent you a TPA request.");
		map.put("command.tpa.msg.accept", "[Click here to agree]");
		map.put("command.back.failed", "Back failed");
		map.put("command.back.success", "Back successfully");
		map.put("command.home.failed", "Back home failed");
		map.put("command.home.success", "Back home successfully");
		map.put("command.set_home.failed", "Set home failed");
		map.put("command.set_home.success", "Set home successfully");
		map.put("ban.item.deny", "Banned items cleared");
		map.put("login.menu.button.clear", "Clear");
		map.put("login.menu.button.login", "Login");
		map.put("login.menu.title", "Please input password");
		map.put("login.success", "Login successful");
		map.put("login.failed", "Login failed");
		map.put("trash.command.error.cleaning", "Cleaning up now. Please wait.");
		map.put("trash.command.error.has_player", "A player is checking the trash, please wait.");
		map.put("trash.menu.title", "Trash Bin");
		map.put("trash.menu.button.previous_page", "Previous Page");
		map.put("trash.menu.button.next_page", "Next Page");
		map.put("trash.menu.button.home_page", "Home Page");
		map.put("trash.menu.button.tips", "--");
		map.put("blacklist.deny", "You do not have permission to join the current server");
		map.put("clear.entity.limit", "Discovery of a large number of entities to begin liquidation");
		map.put("clear.entity.done", "clear $clear.count entity");
		map.put("clear.item.done", "clear $clear.count item,send '/st trash' to open the trash bin");
		map.put("clear.normal.1", "clear in 1 second");
		map.put("clear.normal.2", "clear in 2 second");
		map.put("clear.normal.3", "clear in 3 second");
		map.put("clear.normal.4", "clear in 4 second");
		map.put("clear.normal.5", "clear in 5 second");
		map.put("clear.normal.6", "clear in 6 second");
		map.put("clear.normal.7", "clear in 7 second");
		map.put("clear.normal.8", "clear in 8 second");
		map.put("clear.normal.9", "clear in 9 second");
		map.put("clear.normal.10", "clear in 10 second");
		map.put("clear.normal.30", "clear in 30 second");
		map.put("clear.normal.60", "clear in 60 second");
		map.put("reload.success.all", "Reload all config successfully.");
		map.put("reload.success.config", "Reload config successfully.");
		map.put("reload.success.player_group", "Reload player group successfully.");
		map.put("reload.success.clear", "Reload clear successfully.");
		map.put("reload.success.msg", "Reload msg successfully.");
		map.put("reload.success.black_list", "Reload black list successfully.");
		map.put("reload.success.language", "Reload language successfully.");
		map.put("get_server_info.command.listen.failed", "Get server info failed");
		map.put("get_help.command.listen.help", "-----help-----");
		map.put("get_server_info.command.listen.info", "-----info-----");
		map.put("get_server_info.command.listen.host", "host");
		map.put("get_server_info.command.listen.port", "port");
		map.put("get_server_info.command.listen.description", "description");
		map.put("get_server_info.command.listen.version", "version");

		map.put("set_lang.command.success", "set language success");
		map.put("set_lang.command.failed", "set language failed");
		map.put("set_lang.command.wainning","the code language not load");
		return map;
	}




	public static String getLanguage() {
		String lang = Config.I.map(configData -> configData.lang).orElse("");
		if (lang.isBlank() || lang.equals("auto")) {
			Locale defaultLocale = Locale.getDefault();
			lang = defaultLocale.getLanguage() + "_" + defaultLocale.getCountry();
		}
		lang = lang.toLowerCase();
		File file = new File(_File.getFilePath(ConfigDir.LANGUAGE, lang + ".json"));
		if (!file.exists()) {
			return "en_us";
		}
		return file.getName();
	}

	public static String translatable(String key,Object... value) {
		return translatable("en_us", key,value);
	}
	public static String translatable(ServerPlayer serverPlayer,String key,Object... value) {
		if (serverPlayer == null) return translatable("",key, key);
		return translatable(PlayerConfig.get(serverPlayer).lang(),key, key,value);
	}
	public static String translatable(String lang,String key,Object... value) {
		return translatable(lang,key, key,value);
	}

	public static String translatable(String lang,String key,String def,Object... value){
		if (lang.isBlank()) lang = "en_us";
		Language language = LANG.getOrDefault(lang + _SuffixCDT.JSON_SUFFIX,DEFAULT_LANG);
		String msg = language.map(map -> map.get(key)).orElse(DEFAULT_LANG.map(m -> m.get(key)).orElse(def));
		return MessageFormat.format(msg,value);
	}

	public static Component getComponent(ServerPlayer serverPlayer, String key) {
		return Component.literal(translatable(serverPlayer,key));
	}


}

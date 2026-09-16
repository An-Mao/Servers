package dev.anye.mc.st.config.lang;

import dev.anye.mc.st.ST;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;

public final class LanguageDefault {
	private LanguageDefault(){}
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
		map.put("set_lang.command.wainning", "the code language not load");


		map.put("currency.st.name", "MeowCoins");

		map.put("sell.command.item.success", "sell item success");
		map.put("sell.command.item.failed", "sell item failed");


		map.put("command.st.my", "PLAYER UUID：$uuid\\n CURRENCY：$currency");

		map.put("shelf.st.item.player.error.write_log", "Currency log write error");
		map.put("shelf.st.item.player.error.locked", "Currency system is locked");
		map.put("shelf.st.item.player.error.insufficient_funds", "Insufficient funds");
		map.put("shelf.st.item.player.error.currency_overrun", "Currency overrun");
		map.put("shelf.st.item.player.error.price", "Currency range error");
		map.put("currency.st.player.config.error", "Player currency system is error");
		map.put("shelf.st.item.config.error", "Item Shelf Config is error");
		map.put("shelf.st.item.buy.error.not_have", "The item is empty");
		return map;
	}
}

package dev.anye.mc.st.config.ban_item;

import java.util.List;

public record BanItemData (
		boolean enable,
		String msg,
		List<String> bannedItems) {
	public static final BanItemData DEFAULT = new BanItemData(
			true,
			"ban.item.deny",
			List.of("minecraft:bedrock"));
}

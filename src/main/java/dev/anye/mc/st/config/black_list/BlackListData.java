package dev.anye.mc.st.config.black_list;

import java.util.List;

public record BlackListData (
		boolean enable,
		boolean allowedMode,
		String msg,
		List<String> list) {
	public static final BlackListData DEFAULT = new BlackListData(
			true,
			false,
			"blacklist.deny",
			List.of()
	);
}

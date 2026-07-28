package dev.anye.mc.st.config.login;

import java.util.HashMap;
import java.util.Map;

public record LoginData(
	boolean enable,
	String success,
	String fail,
	int time,
	Map<String, String> passwords) {
	public static final LoginData DEFAULT = new LoginData(
			true,
			"login.success",
			"login.failed",
			1200,
			new HashMap<>()
	);
}

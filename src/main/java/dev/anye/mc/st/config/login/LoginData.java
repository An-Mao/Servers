package dev.anye.mc.st.config.login;

public record LoginData(
	boolean enable,
	String success,
	String fail,
	int time
) {
	public static final LoginData DEFAULT = new LoginData(
			true,
			"login.success",
			"login.failed",
			1200
	);
}

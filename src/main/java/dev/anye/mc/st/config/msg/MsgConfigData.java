package dev.anye.mc.st.config.msg;

public record MsgConfigData(
		boolean enable,
		String msg) {
	public static final MsgConfigData FIRST_JOIN_DEFAULT = new MsgConfigData(true,"This is First Join Message!");
	public static final MsgConfigData EVERY_DAY_JOIN_DEFAULT = new MsgConfigData(true,"This is Every Day Join Message!");
	public static final MsgConfigData EVERY_JOIN_DEFAULT = new MsgConfigData(true,"This is Every Join Message!");
}

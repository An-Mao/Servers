package dev.anye.mc.st.config.command;

public record CommandData(
	String commandRoot,
	boolean home,
	boolean setHome,
	boolean back,
	int backMaxCount,
	boolean tpa,
	boolean tpaAccept,
	boolean tpaDeny,
	boolean reward,
	boolean trash,
	boolean setLang) {
	public static final CommandData DEFAULT = new CommandData(
			"st",
			true,
			true,
			true,
			5,
			true,
			true,
			true,
			true,
			true,
			true
	);
}
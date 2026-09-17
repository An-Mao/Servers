package dev.anye.mc.st.config.currency.shelf;

public record PlayerSystemShelfItemLogData(int count, long lastTime) {
	public static final PlayerSystemShelfItemLogData EMPTY = new PlayerSystemShelfItemLogData(0,0);
}

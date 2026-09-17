package dev.anye.mc.st.config.currency.shelf;


public record SystemShelfItemLogData(int count,long lastTime) {
	public static final SystemShelfItemLogData EMPTY = new SystemShelfItemLogData(0,0);
}

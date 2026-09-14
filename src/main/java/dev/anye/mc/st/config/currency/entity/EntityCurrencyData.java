package dev.anye.mc.st.config.currency.entity;

public record EntityCurrencyData(double value, double maxValue, long cooldown){
	public static final EntityCurrencyData DEFAULT = new EntityCurrencyData(0, 0, -1);
}

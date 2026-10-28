package dev.anye.mc.st.config.currency.entity;

import java.math.BigDecimal;

public class PlayerEntityCurrencyData {
	public static final PlayerEntityCurrencyData DEFAULT = new PlayerEntityCurrencyData();

	private BigDecimal value = BigDecimal.ZERO;
	private long last = 0;

	public BigDecimal getValue() {
		return value;
	}

	public long getLast() {
		return last;
	}

	public void setLast(long last) {
		this.last = last;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void add(double v){
		add(BigDecimal.valueOf(v));
	}
	public void add(BigDecimal v){
		value = value.add(v);
	}
}

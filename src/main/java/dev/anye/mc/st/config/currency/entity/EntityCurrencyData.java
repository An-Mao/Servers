package dev.anye.mc.st.config.currency.entity;

import java.math.BigDecimal;

public record EntityCurrencyData(BigDecimal value, BigDecimal maxValue, long cooldown){
	public static final EntityCurrencyData DEFAULT = new EntityCurrencyData(BigDecimal.ZERO, BigDecimal.ZERO, -1);

	public boolean isValid(){
		return value.compareTo(BigDecimal.ZERO) > 0 && maxValue.compareTo(BigDecimal.ZERO) > 0;
	}
}

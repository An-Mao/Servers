package dev.anye.mc.st.config.currency;

import dev.anye.mc.st.ST;

import java.math.BigDecimal;

public record PlayerCurrencyLogData (String source, BigDecimal number){
	public PlayerCurrencyLogData(String source, BigDecimal number){
		this.source = "["+ ST.FAST_DATE_TIME.toTimeString(":")+"]"+source;
		this.number = number;
	}
}

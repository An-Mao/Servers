package dev.anye.mc.st.config.currency;

import dev.anye.mc.st.ST;

public record PlayerCurrencyLogData (String source, double number){
	public PlayerCurrencyLogData(String source, double number){
		this.source = "["+ ST.FAST_DATE_TIME.toTimeString(":")+"]"+source;
		this.number = number;
	}
}

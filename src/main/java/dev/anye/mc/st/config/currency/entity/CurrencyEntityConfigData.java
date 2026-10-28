package dev.anye.mc.st.config.currency.entity;

import java.math.BigDecimal;

public record CurrencyEntityConfigData (BigDecimal maxValue, long cooldown){
}

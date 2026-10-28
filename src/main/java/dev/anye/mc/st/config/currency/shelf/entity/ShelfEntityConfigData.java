package dev.anye.mc.st.config.currency.shelf.entity;


import dev.anye.mc.st.config.currency.shelf.ShelfItemConfigData;

import java.math.BigDecimal;

public record ShelfEntityConfigData(
		boolean enable, int type,
		BigDecimal fee, boolean percentageFee,
		BigDecimal tax, boolean percentageTax,
		BigDecimal minPrice, BigDecimal maxPrice) {
	public static final ShelfEntityConfigData DEFAULT = new ShelfEntityConfigData(true,2, ShelfItemConfigData.defaultFee,false,ShelfItemConfigData.defaultTax,true,BigDecimal.ZERO,BigDecimal.ZERO);

	public BigDecimal getFee(BigDecimal price){
		return percentageFee ? price.multiply(fee) : fee;
	}
	public BigDecimal getTax(BigDecimal price){
		return percentageTax ? price.multiply(tax) : tax;
	}
	public boolean checkPrice(BigDecimal price){
		return price.compareTo(minPrice) > 0 &&(maxPrice.compareTo(BigDecimal.ZERO) == 0 || price.compareTo(minPrice) < 0);
	}
}

package dev.anye.mc.st.config.currency.shelf;

import java.math.BigDecimal;

/**
 * 货架物品配置数据类
 * @param enable 是否启用，禁用后玩家不能上架物品到货架
 * @param fee 手续费，上架物品时扣除，扣除失败则不上架。出售成功或下架物品不应该返还。
 * @param percentageFee 百分比费率，启用后将按照设置的价格百分比收取手续费。
 * @param tax 税，物品成交时扣除。实际所得 = 物品售价 - 税
 * @param percentageTax 百分比税率
 * @param minPrice 最小价格
 * @param maxPrice 最大价格
 */
public record ShelfItemConfigData(
		boolean enable,
		BigDecimal fee, boolean percentageFee,
		BigDecimal tax, boolean percentageTax,
		BigDecimal minPrice, BigDecimal maxPrice
){
	public static final BigDecimal defaultFee = new BigDecimal("5");
	public static final BigDecimal defaultTax = new BigDecimal("0.1");
	public static final ShelfItemConfigData DEFAULT = new ShelfItemConfigData(true, defaultFee,false, defaultTax,true,BigDecimal.ZERO,BigDecimal.ZERO);


	public BigDecimal getFee(BigDecimal price){
		return percentageFee ? price.multiply(fee) : fee;
	}
	public BigDecimal getTax(BigDecimal price){
		return percentageTax ? price.multiply(tax) : tax;
	}
	public boolean checkPrice(BigDecimal price){
		return price.compareTo(minPrice) > 0 &&(maxPrice.compareTo(BigDecimal.ZERO) == 0 || price.compareTo(minPrice) < 0);
	}

	/*public double getFee(double price){
		return percentageFee ? price * fee : fee;
	}
	public double getTax(double price){
		return percentageTax ? price * tax : tax;
	}
	public boolean checkPrice(double price){
		return price > minPrice && price < (maxPrice == 0 ? Double.MAX_VALUE : maxPrice);
	}*/
}

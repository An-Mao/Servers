package dev.anye.mc.st.config.currency.shelf;

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
		double fee, boolean percentageFee,
		double tax, boolean percentageTax,
		double minPrice, double maxPrice
){
	public static final ShelfItemConfigData DEFAULT = new ShelfItemConfigData(true,10,false,0.1,true,0,0);

	public double getFee(double price){
		return percentageFee ? price * fee : fee;
	}
	public double getTax(double price){
		return percentageTax ? price * tax : tax;
	}
	public boolean checkPrice(double price){
		return price > minPrice && price < (maxPrice == 0 ? Double.MAX_VALUE : maxPrice);
	}
}

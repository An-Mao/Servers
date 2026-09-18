package dev.anye.mc.st.config.currency.shelf.entity;


public record ShelfEntityConfigData(
		boolean enable,int type,
		double fee, boolean percentageFee,
		double tax, boolean percentageTax,
		double minPrice, double maxPrice) {
	public static final ShelfEntityConfigData DEFAULT = new ShelfEntityConfigData(true,2,10,false,0.1,true,0,0);

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

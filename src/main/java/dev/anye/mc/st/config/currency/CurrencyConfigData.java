package dev.anye.mc.st.config.currency;

public class CurrencyConfigData {
	private boolean enable = true;
	private boolean block = true;
	private boolean entity = true;

	private double blockDefaultCurrency = 0;
	private double entityDefaultCurrency = 0;

	private boolean sellItem = true;
	private boolean sellEntity = true;
	private boolean sellXp = true;

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	public void setSellItem(boolean sellItem) {
		this.sellItem = sellItem;
	}

	public void setSellEntity(boolean sellEntity) {
		this.sellEntity = sellEntity;
	}

	public void setEntityDefaultCurrency(double entityDefaultCurrency) {
		this.entityDefaultCurrency = entityDefaultCurrency;
	}

	public boolean isEnableBlock(){
		return enable && block;
	}
	public boolean isEnableEntity(){
		return enable && entity;
	}

	public boolean isEnableSellItem(){
		return enable && sellItem;
	}
	public boolean isEnableSellEntity(){
		return enable && sellEntity;
	}
	public boolean isEnableSellXp(){
		return enable && sellXp;
	}


	public boolean isEnable(){
		return enable;
	}

	public double blockDefaultCurrency() {
		return blockDefaultCurrency;
	}
}

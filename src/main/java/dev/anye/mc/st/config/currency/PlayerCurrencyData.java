package dev.anye.mc.st.config.currency;

import dev.anye.mc.st.helper.MsgHelper;

import java.math.BigDecimal;

public final class PlayerCurrencyData {
	private BigDecimal currency;
	private boolean lock;

	public PlayerCurrencyData(){
		this("0",false);
	}
	public PlayerCurrencyData(String currency,boolean lock){
		this.currency = new BigDecimal(currency);
		this.lock = lock;
	}

	public void lock(){
		this.lock = true;
	}
	public void unlock(){
		this.lock = false;
	}
	public boolean isLock(){
		return lock;
	}

	public BigDecimal currency(){
		return currency;
	}
	public boolean check(){
		return check(BigDecimal.ZERO);
	}

	public boolean check(double v){
		return check(BigDecimal.valueOf(v));
	}

	/**
	 * 判断当前值与给定值的大小
	 * @param v 给定值
	 * @return 如果小于则返回false，大于或等于返回true
	 */
	public boolean check(BigDecimal v){
		return currency.compareTo(v) >= 0;
	}

	public void add(double value){
		add(BigDecimal.valueOf(value));
	}
	public void sub(double value){
		sub(BigDecimal.valueOf(value));
	}

	public void add(BigDecimal value){
		currency = currency.add(value);
	}
	public void sub(BigDecimal value){
		currency = currency.subtract(value);
	}

	public boolean addAndWriteLog(String uuid,BigDecimal value,String source){
		if (lock) return false;
		if (PlayerCurrencyLog.writeLog(uuid,source+"=>+"+value)) {
			add(value);
			return true;
		}
		return false;
	}
	public boolean subAndLog(String uuid,BigDecimal value,String source){
		if (lock) return false;
		if (!check(value)) {
			//MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.insufficient_funds");
			return false;
		}
		if (PlayerCurrencyLog.writeLog(uuid,source+"=>-"+value)) {
			sub(value);
			return true;
		}
		//MsgHelper.sendMsgToPlayerF(off,"shelf.st.item.player.error.write_log");
		return false;
	}


}
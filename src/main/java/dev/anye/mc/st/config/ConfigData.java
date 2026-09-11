package dev.anye.mc.st.config;

public class ConfigData {
	public boolean clearAnomalousEntity;
	public String lang;
	public boolean enchantmentExtract;

	public ConfigData(
			boolean clearAnomalousEntity,
			String lang,
			boolean enchantmentExtract
	){
		this.clearAnomalousEntity = clearAnomalousEntity;
		this.lang = lang;
		this.enchantmentExtract = enchantmentExtract;
	}

	public static ConfigData Default(){
		return new ConfigData(true,"auto",true);
	}
}

package dev.anye.mc.st.config;

public class ConfigData {
	public boolean clearAnomalousEntity = true;
	public String lang = "auto";
	public boolean enchantmentExtract = true;


	public static ConfigData Default(){
		return new ConfigData();
	}
}

package dev.anye.mc.st.config.player_data;

import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.data_type.PosData;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
	public PosData home;
	public List<PosData> backs;
	private String lang;

	public PlayerData(PosData home, List<PosData> backs, String lang){
		this.home = home;
		this.backs = backs;
		this.lang = lang;

	}
	public PlayerData() {
		this(new PosData(),new ArrayList<>(),"en_us");
	}
	public void setLang(String lang) {
		this.lang = lang;
	}

	public String lang(){
		return lang;
	}

	public PosData getHome() {
		return home;
	}


	public void addBack(PosData pd) {
		if (backs == null) backs = new ArrayList<>();
		else if (backs.size() >= CommandConfig.I.getData().backMaxCount()) backs.removeFirst();
		backs.add(pd);
	}
}

package dev.anye.mc.st.config.player$data;

import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.data$type.PosData;

import java.util.ArrayList;
import java.util.List;

public class PD {
	public PosData home;
	public List<PosData> backs;

	public PD() {
		home = new PosData();
		backs = new ArrayList<>();
	}

	public PosData getHome() {
		return home;
	}


	public void addBack(PosData pd) {
		if (backs == null) backs = new ArrayList<>();
		else if (backs.size() >= CommandConfig.I.getDatas().backMaxCount) backs.removeFirst();
		backs.add(pd);
	}
}

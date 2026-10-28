package dev.anye.mc.st.config.player_data;

import dev.anye.core.bytes._Byte;
import dev.anye.mc.st.config.command.CommandConfig;
import dev.anye.mc.st.config.command.CommandData;
import dev.anye.mc.st.data_type.PosData;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
	private static final String S = _Byte.getMd5("%A%*n%Y%(e%");
	public PosData home;
	public List<PosData> backs;
	private String lang;
	private String password;

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
		else if (backs.size() >= CommandConfig.I.fetch(CommandData::backMaxCount,0)) backs.removeFirst();
		backs.add(pd);
	}

	public boolean emptyPassword() {
		return this.password == null || this.password.isBlank();
	}

	public final void setPassword(ServerPlayer serverPlayer, String newPassword){
		this.password = encodePassword(serverPlayer,newPassword);
	}

	private String encodePassword(ServerPlayer serverPlayer, String password) {
		return _Byte.getHash(_Byte.getMd5(serverPlayer.getStringUUID()) + S + _Byte.getMd5(password),"SHA-256");
	}

	public final boolean checkPassword(ServerPlayer serverPlayer,String password){
		if (emptyPassword()) return true;
		return this.password.equals(encodePassword(serverPlayer, password));
	}
}

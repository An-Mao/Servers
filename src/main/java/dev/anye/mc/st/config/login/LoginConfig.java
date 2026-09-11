package dev.anye.mc.st.config.login;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.LoginHelper;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class LoginConfig extends _JsonConfigS<LoginData> {
	public static final String FILE = _File.getFilePath(ConfigDir.LOGIN ,"Login.json");
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final LoginConfig INSTANCE = new LoginConfig();

	public LoginConfig() {
		super(FILE, LoginData.DEFAULT, new TypeToken<>() {});
	}

	public void openLogin(ServerPlayer serverPlayer){
		read(loginData -> {
			if (loginData.enable()) LoginHelper.openLogin(serverPlayer);
		});
	}

	public Map<String, String> getPasswords() {
		return read(LoginData::passwords,new HashMap<>());
	}
}

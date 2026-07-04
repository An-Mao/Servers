package dev.anye.mc.st.config.login;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;

import java.util.HashMap;

public class LoginConfig extends _JsonConfig<LoginData> {
	public static final String file = _File.getFilePath(ConfigDir.LOGIN + "Login.json");
	public static final LoginConfig INSTANCE = new LoginConfig();

	public LoginConfig() {
		super(file, """
				{
				    "enable": true,
				    "time": 1200,
				    "success": "login.success",
				    "fail": "login.failed",
				    "password": {}
				}
				""", new TypeToken<>() {
		});
	}

	public HashMap<String, String> getPasswords() {
		if (getDatas().getPasswords() == null) getDatas().setPasswords(new HashMap<>());
		return getDatas().getPasswords();
	}
}

package dev.anye.mc.st.config.black$list;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.ConfigDir;

public class BlackListConfig extends _JsonConfig<BlackListData> {
    private static final String filePath = _File.getFilePath(ConfigDir.BLACK_LIST , "blackList.json");
    public static final BlackListConfig instance = new BlackListConfig();
    public BlackListConfig() {
        super(filePath, """
                {
                    "enable": true,
                    "allowedMode": false,
                    "msg": "blacklist.deny",
                    "list": []
                }
                """, new TypeToken<>() {});
    }
}

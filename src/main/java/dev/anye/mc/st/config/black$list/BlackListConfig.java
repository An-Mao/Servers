package dev.anye.mc.st.config.black$list;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.mc.st.ST;

public class BlackListConfig extends _JsonConfig<BlackListData> {
    private static final String filePath = ST.ConfigDir_BlackList + "blackList.json";
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

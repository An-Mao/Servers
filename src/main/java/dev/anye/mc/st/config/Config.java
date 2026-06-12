package dev.anye.mc.st.config;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;

public class Config extends _JsonConfig<ConfigData> {
    private static final String file = _File.getFilePath(ConfigDir.BASE , "config.json");
    public static final Config I = new Config();
    public Config() {
        super(file, """
                {
                    "clearAnomalousEntity": true,
                    "lang": "auto"
                }
                """, new TypeToken<>(){});
    }
}

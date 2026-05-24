package dev.anye.mc.st.config.command;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.mc.st.ST;

public class CommandConfig extends _JsonConfig<CommandData> {
    public static final String filePath = ST.ConfigDir + "Commands.json";
    public static final CommandConfig I = new CommandConfig();
    public CommandConfig() {
        super(filePath, """
                {
                    "commandRoot": "st",
                    "home": true,
                    "setHome": true,
                    "back": true,
                    "backMaxCount": 5,
                    "tpa": true,
                    "tpaAccept": true,
                    "tpaDeny": true,
                    "reward": true,
                    "trash": true
                }
                """, new TypeToken<>() {});
    }
}

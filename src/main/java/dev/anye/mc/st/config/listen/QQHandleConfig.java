package dev.anye.mc.st.config.listen;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.config.Language;

public class QQHandleConfig extends _JsonConfig<QQHandleConfigData> {
    private static final String file = _File.getFilePath(ConfigDir.LISTEN_HANDLE , "TencentQQ.json");
    public static final QQHandleConfig I = new QQHandleConfig();
    public static final Language Command = new Language(_File.getFilePath(ConfigDir.LISTEN_HANDLE , "TencentQQCommand.json"), """
            {
                "getServerInfo": "服务器信息",
                "getServerList": "玩家列表",
                "getHelp": "帮助"
            }
            """,true);
    public QQHandleConfig() {
        super(file, """
                {
                    "enable": true,
                    "group":["1234567","1234567"],
                    "serverHost": "192.168.2.223:25565",
                    "qqHost": "192.168.2.223:3000",
                    "groupMsgUrl": "http://192.168.2.223:3000/send_group_msg"
                }
                """, new TypeToken<>() {});
    }
    public boolean isEnable() {
        return getDatas().enable;
    }
    public String getHost() {
        return getDatas().qqHost;
    }
    public String getGroupMsgUrl() {
        return getDatas().groupMsgUrl;
    }

    public boolean enableGroup(String s) {
        if (getDatas().group != null) return getDatas().group.contains(s);
        return false;
    }

    public String getServerHost() {
        return getDatas().serverHost;
    }
}

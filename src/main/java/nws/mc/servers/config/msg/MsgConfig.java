package nws.mc.servers.config.msg;

import com.google.gson.reflect.TypeToken;
import net.minecraft.server.level.ServerPlayer;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;
import nws.mc.servers.helper.MsgHelper;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MsgConfig{
    public static final FirstJoin firstJoin = new FirstJoin();
    public static final EveryDayJoin everyDayJoin = new EveryDayJoin();
    public static final EveryJoin everyJoin = new EveryJoin();


    public static class FirstJoin extends _JsonConfig<MsgConfigData>{
        private static final String file = Servers.ConfigDir_Msg + "FirstJoin.json";
        public final FirstLog firstLog = new FirstLog();
        public FirstJoin() {
            super(file, """
                    {
                        "enable": true,
                        "msg": "This is First Join Message!"
                    }
                    """, new TypeToken<>(){});
        }
        public void send(ServerPlayer serverPlayer){
            if (firstLog.getDatas().contains(serverPlayer.getStringUUID())) return;
            MsgHelper.sendMsgToPlayerF(serverPlayer, getDatas().getMsg());
            firstLog.getDatas().add(serverPlayer.getStringUUID());
            firstLog.save();
        }
    }
    public static class EveryDayJoin extends _JsonConfig<MsgConfigData>{
        private static final String file = Servers.ConfigDir_Msg + "EveryDayJoin.json";
        private static String t = getDay();
        public final DayLog dayLog = new DayLog();
        public EveryDayJoin() {
            super(file, """
                    {
                        "enable": true,
                        "msg": "This is Every Day Join Message!"
                    }
                    """, new TypeToken<>(){});
        }
        public void send(ServerPlayer serverPlayer){
            List<String> list = dayLog.getDatas().getOrDefault(t, new ArrayList<>());
            if (t.equals(getDay())) {
                if (!dayLog.getDatas().getOrDefault(t,new ArrayList<>()).contains(serverPlayer.getStringUUID())) {
                    MsgHelper.sendMsgToPlayerF(serverPlayer, getDatas().getMsg());
                }else {
                    return;
                }
            }else {
                MsgHelper.sendMsgToPlayerF(serverPlayer, getDatas().getMsg());
                t = getDay();
                dayLog.getDatas().clear();
            }
            list.add(serverPlayer.getStringUUID());
            dayLog.getDatas().put(t, list);
            dayLog.save();
        }

        public static String getDay(){
            return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
        }
    }
    public static class EveryJoin extends _JsonConfig<MsgConfigData>{
        private static final String file = Servers.ConfigDir_Msg + "EveryJoin.json";
        public EveryJoin() {
            super(file, """
                    {
                        "enable": true,
                        "msg": "This is Every Join Message!"
                    }
                    """, new TypeToken<>(){});
        }
        public void send(ServerPlayer serverPlayer){
            MsgHelper.sendMsgToPlayerF(serverPlayer,getDatas().getMsg());

        }
    }


    public static class FirstLog extends _JsonConfig<List<String>> {
        private static final String file = Servers.ConfigDir_Msg + "FirstLog.json";
        public FirstLog() {
            super(file, "", new TypeToken<>(){});
        }
        @Override
        public List<String> getDatas() {
            if (datas == null) datas = new ArrayList<>();
            return datas;
        }
    }
    public static class DayLog extends _JsonConfig<HashMap<String,List<String>>> {
        private static final String file = Servers.ConfigDir_Msg + "DayLog.json";
        public DayLog() {
            super(file, "", new TypeToken<>(){});
        }

        @Override
        public HashMap<String, List<String>> getDatas() {
            if (datas == null) datas = new HashMap<>();
            return datas;
        }
    }
}

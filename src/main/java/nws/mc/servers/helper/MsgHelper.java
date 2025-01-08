package nws.mc.servers.helper;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nws.dev.core.format._FormatToString;
import nws.dev.core.javascript._EasyJS;
import nws.mc.servers.Servers;
import nws.mc.servers.config.Language;

import java.util.HashMap;

public class MsgHelper {

    public static HashMap<String, String> createMsgMap(String a, String b){
        HashMap<String, String> map = new HashMap<>();
        map.put(a,b);
        return map;
    }
    public static void sendServerMsg(MinecraftServer server, String msg){
        if (msg.isEmpty() || server == null) return;
        String M = Language.getOrDefault(msg, msg);
        server.getPlayerList().getPlayers().forEach(serverPlayer -> MsgHelper.sendMsgToPlayer(serverPlayer,M));
    }

    public static void sendServerMsg(MinecraftServer server, String msg, HashMap<String, String> map){
        if (msg.isEmpty() || server == null) return;
        String[] M = {Language.getOrDefault(msg, msg)};
        map.forEach((s, s2) -> M[0] = M[0].replace(s, s2));
        server.getPlayerList().getPlayers().forEach(serverPlayer -> MsgHelper.sendMsgToPlayer(serverPlayer,M[0]));
    }
    public static void sendServerMsg(ServerLevel serverLevel, String msg){
        if (msg.isEmpty()  || serverLevel == null)return;
        sendServerMsg(serverLevel.getServer(),msg);
        //serverLevel.getServer().getPlayerList().getPlayers().forEach(serverPlayer -> MsgHelper.sendServerMsg(serverPlayer,msg));
    }



    public static void sendMsgToPlayerF(ServerPlayer serverPlayer, String msg){
        if (msg.isEmpty())return;
        sendMsgToPlayer(serverPlayer,Language.getOrDefault(msg, msg));

    }
    public static void sendMsgToPlayerF(ServerPlayer serverPlayer, String msg, HashMap<String, String> map){
        if (msg.isEmpty()) return;
        String[] M = {Language.getOrDefault(msg, msg)};
        map.forEach((s, s2) -> M[0] = M[0].replace(s, s2));
        sendMsgToPlayer(serverPlayer,M[0]);
    }
    private static void sendMsgToPlayer(ServerPlayer serverPlayer, String msg){
        if (msg.isEmpty()) return;
        msg = MsgHelper.format(serverPlayer,msg,new HashMap<>());
        if (msg.isEmpty()) return;
        serverPlayer.sendSystemMessage(Component.literal(msg));
        //serverPlayer.connection.sendDisguisedChatMessage(Component.literal(msg), ChatType.bind(ChatType.SAY_COMMAND,serverPlayer));
    }
    private static String format(ServerPlayer player, String msg,HashMap<String, Object> map) {
        if (msg.toLowerCase().endsWith(".js")) {
            _EasyJS easyJS = _EasyJS.creat();
            easyJS.addParameter("player", player);
            map.forEach(easyJS::addParameter);
            Object result = easyJS.runFile(Servers.ConfigDir_JavaScript+msg);
            if (result != null){
                return result.toString();
            }
            // "error js: "+msg;
            return "";
        }else {
            if (!map.isEmpty()) {
                String[] m = {msg};
                map.forEach((s, o) -> {
                    if (o instanceof String s1) m[0] = m[0].replace(s, s1);
                    else if (o instanceof Number n) m[0] = m[0].replace(s, n.toString());
                });
                msg = m[0];
            }
            msg = msg.replace("$player.name", player.getName().getString());
            msg = msg.replace("$player.health", _FormatToString.formatValue(player.getHealth(), 2));
            msg = msg.replace("$player.maxHealth", _FormatToString.formatValue(player.getMaxHealth(), 2));
            msg = msg.replace("$player.expLevel", _FormatToString.formatValue(player.experienceLevel, 2));
            return msg;
        }
    }




    public static void sendWithArgs(ServerPlayer serverPlayer, String msg, HashMap<String, Object> map){
        if (msg.isEmpty()) return;
        msg = MsgHelper.format(serverPlayer,msg,map);
        if (msg.isEmpty()) return;
        serverPlayer.sendSystemMessage(Component.literal(msg));
    }
    public static void sendWithArgs(MinecraftServer server, String msg, HashMap<String, Object> map){
        if (msg.isEmpty()) return;
        server.getPlayerList().getPlayers().forEach(serverPlayer -> MsgHelper.sendWithArgs(serverPlayer,msg,map));
    }
}

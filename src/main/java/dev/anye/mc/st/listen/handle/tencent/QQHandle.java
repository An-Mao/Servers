package dev.anye.mc.st.listen.handle.tencent;

import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import dev.anye.core.net._Net;
import dev.anye.mc.st.config.Language;
import dev.anye.mc.st.config.listen.QQHandleConfig;
import dev.anye.mc.st.listen.ListenHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QQHandle extends ListenHandle {
    private final Map<String, Command> usualCommand;
    private final List<String> commands = new ArrayList<>();
    public QQHandle() {
        super();
        usualCommand = Map.of(
                getCommand("getServerInfo"), this::getServerInfo,
                getCommand("getServerList"), this::getServerList,
                getCommand("getHelp"), this::getHelp
        );
        //usualCommand.put(getCommand("getHelp"), this::getHelp);
        //log.debug("usualCommand");
        //log.debug(usualCommand.toString());
        commands.addAll(usualCommand.keySet());
    }

    private String getHelp() {
        return getLang("get_help.command.listen.help")+"\\n"+ commands.toString();
    }

    private String getServerList() {
        return "--";
    }

    @Override
    public boolean isEnable() {
        return QQHandleConfig.I.isEnable();
    }

    @Override
    public boolean handle(String request, StringBuilder response) {
        QQData.GroupMsg groupMsg = toObj(request, QQData.GroupMsg.class);
        //System.out.println(groupMsg);
        if (groupMsg != null && QQHandleConfig.I.enableGroup(groupMsg.group_id())) {
            String msg = normal(groupMsg.raw_message());
            if (!msg.isEmpty()) {
                easySendGroupReplyMsg(groupMsg.group_id(), groupMsg.message_id(), msg);
                response.append("normal success");
                return true;
            }
            response.append("success");
            return true;
        }
        return false;
    }

    public String getCommand(String msg) {
        return QQHandleConfig.Command.getDatas().get(msg);
    }


    private String normal(String msg) {
        Command command = usualCommand.get(msg);
        if (command != null) return command.command();
        return "";
    }
    public String getServerInfo(){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            final StringBuilder list = new StringBuilder(getLang("get_server_info.command.listen.info"));
            list.append("\\n").append(getLang("get_server_info.command.listen.host")).append(":").append(QQHandleConfig.I.getServerHost());
            //list.append("\\n").append(getLang("get_server_info.command.listen.port")).append(":").append(server.getPort());
            ServerStatus status = server.getStatus();
            if (status != null) {
                list.append("\\n").append(getLang("get_server_info.command.listen.description")).append(":").append(status.description().getString());
                list.append("\\n").append(getLang("get_server_info.command.listen.version")).append(":").append(status.version().isPresent() ? status.version().get().name() : "unknown");
            }
            return list.toString();
        }
        return getLang("get_server_info.command.listen.failed");
    }


    public String getLang(String key){
        return Language.get(key);
    }


















    public void sendGroupMsg(String group, QQData.Msg... msg){
        StringBuilder msgs = new StringBuilder();
        for (QQData.Msg m : msg) {
            if (msgs.isEmpty()) msgs.append(m.toString());
            else msgs.append(",").append(m.toString());
        }
        String data = "{\"group_id\":"+group+",\"message\":["+ msgs +"]}";
        //log.debug(data);
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Content-Length", String.valueOf(data.length()));
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String response = _Net.sendData(QQHandleConfig.I.getGroupMsgUrl(),"POST",headers,data);
        //log.debug(response);
    }
    public void easySendGroupMsg(String group, String msg){
        sendGroupMsg(group, QQData.Msg.create(QQData.MsgType.Text, "text", msg));
    }
    public void easySendGroupReplyMsg(String group,String id, String msg){
        sendGroupMsg(group,QQData.Msg.create(QQData.MsgType.Reply, "id", id), QQData.Msg.create(QQData.MsgType.Text, "text", msg));
    }
    public void easySendGroupAtMsg(String group,String qq, String msg){
        sendGroupMsg(group,QQData.Msg.create(QQData.MsgType.At, "qq", qq), QQData.Msg.create(QQData.MsgType.Text, "text", msg));
    }



    public interface Command{
        String command();
    }
}

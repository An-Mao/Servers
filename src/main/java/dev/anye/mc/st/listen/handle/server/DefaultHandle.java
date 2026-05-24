package dev.anye.mc.st.listen.handle.server;

import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import dev.anye.mc.st.config.listen.ListenConfig;
import dev.anye.mc.st.listen.ListenHandle;

public class DefaultHandle extends ListenHandle {

    @Override
    public boolean isEnable() {
        return super.isEnable();
    }

    @Override
    public boolean handle(String req, StringBuilder response) {
        DefaultRequest request = toObj(req, DefaultRequest.class);
        if (request.token().isEmpty() || !request.token().equals(ListenConfig.I.getToken())) return false;
        return switch (request.request()){
            case "command":{
                if (request.data().get("command") instanceof String command){
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null) {
                        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),command);
                        response.append("success");
                        yield true;
                    }
                }
                yield false;
            }
            case "getPlayerList":{
                if (request.data().get("type") instanceof String type){
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null) {
                        final StringBuilder list = new StringBuilder("players:");
                        switch (type) {
                            case "online": {
                                for (String s : server.getPlayerNames()) {
                                    list.append("\n").append(s);
                                }
                            }
                            case "whiteList": {
                                for (String s : server.getPlayerList().getWhiteList().getUserList()) {
                                    list.append("\n").append(s);
                                }
                            }
                        }
                        response.append(list.toString());
                        yield true;
                    }
                }
                yield false;
            }
            case "info":{
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    final StringBuilder list = new StringBuilder("info:");
                    list.append("\nport:").append(server.getPort());
                    ServerStatus status = server.getStatus();
                    if (status != null) {
                        list.append("\ndescription:").append(status.description());
                        list.append("\nversion:").append(status.version().isPresent() ? status.version().get() : "unknown");
                    }
                    response.append(list.toString());
                    yield true;
                }
                yield false;
            }
            default: yield false;
        };
    }
}

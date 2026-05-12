package dev.anye.mc.st.listen;

import com.mojang.logging.LogUtils;
import com.sun.net.httpserver.HttpServer;
import dev.anye.mc.st.config.listen.ListenConfig;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Listen {
    private final Logger log = LogUtils.getLogger();
    public static Listen I = new Listen();
    final HttpServer server;
    public boolean isRun = false;
    public Listen(){
        try {
            server = HttpServer.create(new InetSocketAddress(ListenConfig.I.getPort()), 0);
            server.createContext("/", exchange -> {
                if ("POST".equals(exchange.getRequestMethod())) {
                    InputStream inputStream = exchange.getRequestBody();
                    String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    //log.debug(json);
                    boolean[] c = {true};
                    StringBuilder response = new StringBuilder();
                    ListenHandleRegister.REGISTRY.forEach(listenHandle -> {
                        //System.out.println(listenHandle);
                        if (c[0] && listenHandle.isEnable()) c[0] = listenHandle.handle(json, response);
                    });
                    //Gson gson = new Gson();
                    //ListenRequest request = gson.fromJson(json, ListenRequest.class);
                    //String response =check(request);
                    exchange.sendResponseHeaders(200, response.toString().getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1); // 方法不被允许
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void start(){
        if (isRun) return;
        isRun = true;
        server.start();
    }

    public void close() {
        if (!isRun) return;
        isRun = false;
        server.stop(0);
    }
}

package dev.anye.mc.st.listen.handle.server;

import java.util.HashMap;

public record DefaultRequest(String token, String request, HashMap<String, Object> data) {
    public DefaultRequest(String token, String request) {
        this(token,request, new HashMap<>());
    }
}

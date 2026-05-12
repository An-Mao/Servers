package dev.anye.mc.st.listen;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public abstract class ListenHandle {
    protected final Logger log = LogUtils.getLogger();
    private final Gson gson = new Gson();
    protected boolean enable = false;
    public ListenHandle() {}
    public boolean isEnable() { return enable; }
    public abstract boolean handle(String request,StringBuilder response);
    public <T> T toObj(String str, Class<T> clazz) { return gson.fromJson(str,clazz); }
}

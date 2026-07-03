package dev.anye.mc.st.js;

import net.neoforged.fml.ModList;

public class Js {
    public static final boolean GraalJs = GraalJsIsInstall();
    public static boolean GraalJsIsInstall() {
        return ModList.get().isLoaded("graaljs");
    }
    public static _JavaScript<?> getJsEngine(boolean cache){
        return GraalJs ? new _GraalJS(cache) : new _NashornJS(cache);
    }

}

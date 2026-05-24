package dev.anye.mc.st.js;

import net.minecraftforge.fml.ModList;

public class Js {
    public static final boolean GraalJs = GraalJsIsInstall();
    public static boolean GraalJsIsInstall() {
        return ModList.isLoaded("graaljs");
    }
    public static _JavaScript<?> getJsEngine(boolean cache){
        return GraalJs ? new _GraalJS(cache) : new _NashornJS(cache);
    }

}

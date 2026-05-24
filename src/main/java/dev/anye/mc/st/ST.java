package dev.anye.mc.st;

import dev.anye.core.pack._Pack;
import dev.anye.core.system._File;
import dev.anye.mc.st.listen.ListenHandleRegister;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ST.MOD_ID)
public class ST {
    public static final String MOD_ID = "st";
    public static final String ConfigDir = _File.getFileFullPathWithRun("","ServerTools/");
    public static final String ConfigDir_Language =_File.getFilePath(ConfigDir, "Lang/");
    public static final String ConfigDir_Msg = _File.getFilePath(ConfigDir , "Msg/");
    public static final String ConfigDir_Clear = _File.getFilePath(ConfigDir , "Clear/");
    public static final String ConfigDir_PlayerGroup =_File.getFilePath( ConfigDir , "PlayerGroup/");
    public static final String ConfigDir_BlackList = _File.getFilePath(ConfigDir , "BlackList/");
    public static final String ConfigDir_JavaScript = _File.getFilePath(ConfigDir , "JavaScript/");
    public static final String ConfigDir_Login = _File.getFilePath(ConfigDir , "Login/");
    public static final String ConfigDir_Data = _File.getFilePath(ConfigDir , "Data/");
    public static final String ConfigDir_PlayerData =_File.getFilePath( ConfigDir_Data , "Player/");
    public static final String ConfigDir_ListenHandle = _File.getFilePath(ConfigDir , "ListenHandle/");
    static {
        _File.checkAndCreateDir(ConfigDir);
        _File.checkAndCreateDir(ConfigDir_Language);
        _File.checkAndCreateDir(ConfigDir_Msg);
        _File.checkAndCreateDir(ConfigDir_Clear);
        _File.checkAndCreateDir(ConfigDir_PlayerGroup);
        _File.checkAndCreateDir(ConfigDir_BlackList);
        _File.checkAndCreateDir(ConfigDir_JavaScript);
        _File.checkAndCreateDir(ConfigDir_Login);
        _File.checkAndCreateDir(ConfigDir_Data);
        _File.checkAndCreateDir(ConfigDir_PlayerData);
        _File.checkAndCreateDir(ConfigDir_ListenHandle);


        _Pack.writeFiles("assets/st/lang/", ST.ConfigDir_Language,".json","zh_cn");
        _Pack.writeFiles("assets/st/js/", ST.ConfigDir_JavaScript,".js","TpaRequest");
    }
    public ST(FMLJavaModLoadingContext context) {
        BusGroup modEventBus = context.getModBusGroup();
		ListenHandleRegister.register(modEventBus);
    }
}

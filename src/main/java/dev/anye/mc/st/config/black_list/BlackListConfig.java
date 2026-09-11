package dev.anye.mc.st.config.black_list;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.PlayerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BlackListConfig extends _JsonConfigS<BlackListData> {
	private static final String FILE_PATH = _File.getFilePath(ConfigDir.BLACK_LIST, "blackList.json");
	public static final BlackListConfig INSTANCE = new BlackListConfig();
	public BlackListConfig() {
		super(FILE_PATH, BlackListData.DEFAULT, new TypeToken<>() {});
	}

	public void check(ServerPlayer serverPlayer){
		read(blackListData -> {
			if (blackListData.enable()) {
				if (PlayerHelper.checkPlayer(serverPlayer, blackListData.list())){
					if (!blackListData.allowedMode())
						serverPlayer.connection.disconnect(Component.literal(blackListData.msg()));
				}else {
					if (blackListData.allowedMode())
						serverPlayer.connection.disconnect(Component.literal(blackListData.msg()));
				}
			}
		});
	}
}

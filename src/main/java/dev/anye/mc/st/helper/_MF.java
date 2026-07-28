package dev.anye.mc.st.helper;

import dev.anye.mc.st.data_type.PosData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class _MF {

	public static boolean tp(ServerPlayer player, PosData posData) {
		Identifier res = Identifier.tryParse(posData.Level);
		if (res == null) return false;
		ServerLevel serverLevel = player.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, res));
		if (serverLevel == null) return false;
		player.teleportTo(serverLevel, posData.X, posData.Y, posData.Z, Set.of(), posData.Yaw, posData.xRot, true);
		return true;
	}

}

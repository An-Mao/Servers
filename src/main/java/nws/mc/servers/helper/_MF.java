package nws.mc.servers.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nws.mc.servers.data$type.PosData;

public class _MF {

    public static boolean tp(ServerPlayer player, PosData posData) {
        ResourceLocation res = ResourceLocation.tryParse(posData.Level);
        if (res == null) return false;
        ServerLevel serverLevel = player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, res));
        if (serverLevel == null) return false;
        player.teleportTo(serverLevel,posData.X,posData.Y,posData.Z,posData.Yaw,posData.Pitch);
        return true;
    }

}

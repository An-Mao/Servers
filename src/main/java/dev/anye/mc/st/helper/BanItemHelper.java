package dev.anye.mc.st.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import dev.anye.mc.st.config.ban$item.BanItemConfig;

public class BanItemHelper {
    public static boolean checkItem(ItemStack itemStack) {
        return BanItemConfig.I.isBanned(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
    }
    public static boolean checkItem(ItemLike itemStack) {
        return BanItemConfig.I.isBanned(BuiltInRegistries.ITEM.getKey(itemStack.asItem()).toString());
    }
    public static boolean checkItemAndSend(Entity entity) {
        if (BanItemConfig.I.getDatas().enable && entity instanceof ItemEntity itemEntity){
            ItemStack itemStack = itemEntity.getItem();
            if (BanItemHelper.checkItem(itemStack)) {
                MsgHelper.sendServerMsg(itemEntity.level().getServer(), BanItemConfig.I.getDatas().msg);
                return true;
            }
        }
        return false;
    }
    public static boolean checkItemAndSend(ItemLike itemStack, MinecraftServer server) {
        if (BanItemConfig.I.getDatas().enable){
            if (BanItemHelper.checkItem(itemStack)) {
                MsgHelper.sendServerMsg(server, BanItemConfig.I.getDatas().msg);
                return true;
            }
        }
        return false;
    }
    public static boolean checkItemAndSend(ItemStack itemStack, MinecraftServer server) {
        if (BanItemConfig.I.getDatas().enable){
            if (BanItemHelper.checkItem(itemStack)) {
                MsgHelper.sendServerMsg(server, BanItemConfig.I.getDatas().msg);
                return true;
            }
        }
        return false;
    }
}

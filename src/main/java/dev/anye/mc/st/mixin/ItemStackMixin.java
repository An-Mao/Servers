package dev.anye.mc.st.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import dev.anye.mc.st.helper.BanItemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @ModifyVariable(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemLike ns$init$check(ItemLike value){
        if (BanItemHelper.checkItemAndSend(value, ServerLifecycleHooks.getCurrentServer())){
            value = Items.STONE;
        }
        return value;
    }
}

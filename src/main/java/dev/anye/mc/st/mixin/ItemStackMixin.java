package dev.anye.mc.st.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import dev.anye.mc.st.helper.BanItemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemStack.class)
public class ItemStackMixin {
	@ModifyVariable(method = "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("HEAD"), argsOnly = true,
			ordinal = 0)
	private static Holder<Item> ns$init$check(Holder<Item> item){
		if (BanItemHelper.checkItemAndSend(item.value(), ServerLifecycleHooks.getCurrentServer())){
			item = Holder.direct(Items.STONE);
		}
		return item;
	}

}

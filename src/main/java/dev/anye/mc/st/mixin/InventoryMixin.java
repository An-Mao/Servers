package dev.anye.mc.st.mixin;

import dev.anye.mc.st.config.ban_item.BanItemConfig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
	@Shadow
	@Final
	public Player player;

	@Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
	public void ns$add$check(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {

		if (BanItemConfig.I.checkItemAndSend(itemStack, this.player.level().getServer())) {
			cir.setReturnValue(false);
		}
	}
}

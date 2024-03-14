package pm.c7.scout.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.config.ScoutConfigHandler;
import pm.c7.scout.item.BaseBagItem;

import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(method = "getArrowType", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/RangedWeaponItem;getProjectiles()Ljava/util/function/Predicate;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void scout$arrowsFromBags(ItemStack stack, CallbackInfoReturnable<ItemStack> cir, Predicate<ItemStack> predicate, ItemStack itemStack) {
		if ((boolean) ScoutConfigHandler.getConfigValue("useArrows").value()) {
			var self = (PlayerEntity) (Object) this;
			var leftPouch = ScoutUtil.findBagItem(self, BaseBagItem.BagType.POUCH, false);
			var rightPouch = ScoutUtil.findBagItem(self, BaseBagItem.BagType.POUCH, true);
			var satchel = ScoutUtil.findBagItem(self, BaseBagItem.BagType.SATCHEL, false);

			if (!leftPouch.isEmpty()) {
				BaseBagItem item = (BaseBagItem) leftPouch.getItem();
				var inv = item.getInventory(leftPouch);

				for(int i = 0; i < inv.size(); ++i) {
					ItemStack invStack = inv.getStack(i);
					if (predicate.test(invStack)) {
						cir.setReturnValue(invStack);
					}
				}
			}
			if (!rightPouch.isEmpty()) {
				BaseBagItem item = (BaseBagItem) rightPouch.getItem();
				var inv = item.getInventory(rightPouch);

				for(int i = 0; i < inv.size(); ++i) {
					ItemStack invStack = inv.getStack(i);
					if (predicate.test(invStack)) {
						cir.setReturnValue(invStack);
					}
				}
			}
			if (!satchel.isEmpty()) {
				BaseBagItem item = (BaseBagItem) satchel.getItem();
				var inv = item.getInventory(satchel);

				for(int i = 0; i < inv.size(); ++i) {
					ItemStack invStack = inv.getStack(i);
					if (predicate.test(invStack)) {
						cir.setReturnValue(invStack);
					}
				}
			}
		}
	}
}

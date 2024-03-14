package pm.c7.scout.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.config.ScoutConfigHandler;
import pm.c7.scout.item.BaseBagItem;

@Mixin(BowItem.class)
public class BowItemMixin {
	@Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void scout$arrowsFromBags(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int maxTime, float f) {
		if ((boolean) ScoutConfigHandler.getConfigValue("useArrows").value()) {
			boolean infinity = bl && itemStack.isOf(Items.ARROW);
			boolean hasRan = false;

			if (!infinity && !playerEntity.getAbilities().creativeMode) {
				var leftPouch = ScoutUtil.findBagItem(playerEntity, BaseBagItem.BagType.POUCH, false);
				var rightPouch = ScoutUtil.findBagItem(playerEntity, BaseBagItem.BagType.POUCH, true);
				var satchel = ScoutUtil.findBagItem(playerEntity, BaseBagItem.BagType.SATCHEL, false);

				if (!leftPouch.isEmpty()) {
					BaseBagItem item = (BaseBagItem) leftPouch.getItem();
					var inv = item.getInventory(leftPouch);

					for(int i = 0; i < inv.size(); ++i) {
						ItemStack invStack = inv.getStack(i);
						if (ItemStack.areEqual(invStack, itemStack)) {
							invStack.decrement(1);
							if (invStack.isEmpty()) {
								inv.setStack(i, ItemStack.EMPTY);
							}
							inv.markDirty();
							hasRan = true;
							break;
						}
					}
				}
				if (!rightPouch.isEmpty() && !hasRan) {
					BaseBagItem item = (BaseBagItem) rightPouch.getItem();
					var inv = item.getInventory(rightPouch);

					for(int i = 0; i < inv.size(); ++i) {
						ItemStack invStack = inv.getStack(i);
						if (ItemStack.areEqual(invStack, itemStack)) {
							invStack.decrement(1);
							if (invStack.isEmpty()) {
								inv.setStack(i, ItemStack.EMPTY);
							}
							inv.markDirty();
							hasRan = true;
							break;
						}
					}
				}
				if (!satchel.isEmpty() && !hasRan) {
					BaseBagItem item = (BaseBagItem) satchel.getItem();
					var inv = item.getInventory(satchel);

					for(int i = 0; i < inv.size(); ++i) {
						ItemStack invStack = inv.getStack(i);
						if (ItemStack.areEqual(invStack, itemStack)) {
							invStack.decrement(1);
							if (invStack.isEmpty()) {
								inv.setStack(i, ItemStack.EMPTY);
							}
							inv.markDirty();
							hasRan = true;
							break;
						}
					}
				}
			}
		}
	}
}

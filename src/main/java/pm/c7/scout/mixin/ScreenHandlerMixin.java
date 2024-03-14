package pm.c7.scout.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.scout.ScoutMixin.Transformer;
import pm.c7.scout.ScoutUtil;

@Mixin(ScreenHandler.class)
@Transformer(ScreenHandlerTransformer.class)
public abstract class ScreenHandlerMixin {
	@Inject(method = "getSlot", at = @At("HEAD"), cancellable = true)
	public void scout$fixGetSlot(int index, CallbackInfoReturnable<Slot> cir) {
		var playerScreenHandler = ScoutUtil.getPlayerScreenHandler();
		if (ScoutUtil.isBagSlot(index)) {
			if (playerScreenHandler != null) {
				cir.setReturnValue(ScoutUtil.getBagSlot(index, playerScreenHandler));
			} else {
				cir.setReturnValue(null);
			}
		}
	}

	@Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/ScreenHandler;getCursorStack()Lnet/minecraft/item/ItemStack;", ordinal = 11), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void scout$fixDoubleClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, PlayerInventory playerInventory, Slot slot3) {
		var cursorStack = this.getCursorStack();
		if (!cursorStack.isEmpty() && (!slot3.hasStack() || !slot3.canTakeItems(player))) {
			var slots = ScoutUtil.getAllBagSlots(player.playerScreenHandler);
			var k = button == 0 ? 0 : ScoutUtil.TOTAL_SLOTS - 1;
			var o = button == 0 ? 1 : -1;

			for (int n = 0; n < 2; ++n) {
				for (int p = k; p >= 0 && p < slots.size() && cursorStack.getCount() < cursorStack.getMaxCount(); p += o) {
					Slot slot4 = slots.get(p);
					if (slot4.hasStack() && canInsertItemIntoSlot(slot4, cursorStack, true) && slot4.canTakeItems(player) && this.canInsertIntoSlot(cursorStack, slot4)) {
						ItemStack itemStack6 = slot4.getStack();
						if (n != 0 || itemStack6.getCount() != itemStack6.getMaxCount()) {
							ItemStack itemStack7 = slot4.takeStackRange(itemStack6.getCount(), cursorStack.getMaxCount() - cursorStack.getCount(), player);
							cursorStack.increment(itemStack7.getCount());
						}
					}
				}
			}
		}
	}

	@Shadow
	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
		return false;
	}
	@Shadow
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}
	@Shadow
	public abstract ItemStack getCursorStack();
}
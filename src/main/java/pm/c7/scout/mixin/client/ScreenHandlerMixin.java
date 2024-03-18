package pm.c7.scout.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;

@Environment(EnvType.CLIENT)
@Mixin(value = ScreenHandler.class, priority = 950)
public abstract class ScreenHandlerMixin {
	@Inject(method = "getSlot", at = @At("HEAD"), cancellable = true)
	public void scout$fixGetSlot(int index, CallbackInfoReturnable<Slot> cir) {
		var playerScreenHandler = ScoutUtilClient.getPlayerScreenHandler();
		if (ScoutUtil.isBagSlot(index)) {
			if (playerScreenHandler != null) {
				cir.setReturnValue(ScoutUtil.getBagSlot(index, playerScreenHandler));
			} else {
				cir.setReturnValue(null);
			}
		}
	}
}

package pm.c7.scout.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.DefaultedList;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.server.ScoutUtilServer;

@Environment(EnvType.SERVER)
@Mixin(DefaultedList.class)
public class DefaultedListMixin {
	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	public void scout$fixIndexingSlots(int index, CallbackInfoReturnable<Object> cir) {
		var currentPlayer = ScoutUtilServer.getCurrentPlayer();
		if (ScoutUtil.isBagSlot(index)) {
			if (currentPlayer != null) {
				cir.setReturnValue(ScoutUtil.getBagSlot(index, currentPlayer.playerScreenHandler));
			} else {
				cir.setReturnValue(null);
			}
		}
	}
}

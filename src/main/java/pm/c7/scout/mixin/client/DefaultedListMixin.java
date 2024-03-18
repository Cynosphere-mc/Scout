package pm.c7.scout.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.DefaultedList;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;

@Environment(EnvType.CLIENT)
@Mixin(DefaultedList.class)
public class DefaultedListMixin {
	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	public void scout$fixIndexingSlots(int index, CallbackInfoReturnable<Object> cir) {
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

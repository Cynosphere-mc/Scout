package pm.c7.scout.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

// Lower priority to take priority over Better Recipe Book
@Environment(EnvType.CLIENT)
@Mixin(value = RecipeBookWidget.class, priority = 950)
public class RecipeBookWidgetMixin {
	@Shadow
	protected MinecraftClient client;
	@Shadow
	private int leftOffset;

	@Inject(method = "findLeftEdge", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void scout$modifyRecipeBookPosition(int width, int backgroundWidth, CallbackInfoReturnable<Integer> callbackInfo, int x) {
		if (this.client != null && this.client.player != null && this.isOpen()) {
			ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();

				int columns = (int) Math.ceil(slots / 3);

				// Realign as best we can when "Keep crafting screens centered" is enabled in Better Recipe Book
				if (this.leftOffset != 86) {
					int diff = this.leftOffset - 86;
					x -= diff;
				}

				x += 18 * columns;

				callbackInfo.setReturnValue(x);
			}
		}
	}

	@Shadow
	public boolean isOpen() {
		return false;
	}
}

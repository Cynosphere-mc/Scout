package pm.c7.scout.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipe.book.RecipeBookProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
	private InventoryScreenMixin() {
		super(null, null, null);
	}

	@Inject(method = "isClickOutsideBounds", at = @At("TAIL"), cancellable = true)
	private void scout$adjustOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (this.client != null && this.client.player != null) {
			ItemStack backStack = ScoutUtil.findBagItem(this.client.player, BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				if (mouseY < (top + this.backgroundHeight) + 8 + (18 * rows) && mouseY >= (top + this.backgroundHeight) && mouseX >= left && mouseY < (left + this.backgroundWidth)) {
					callbackInfo.setReturnValue(false);
				}
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= left - (columns * 18) && mouseX < left && mouseY >= (top + this.backgroundHeight) - 90 && mouseY < (top + this.backgroundHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= (left + this.backgroundWidth) && mouseX < (left + this.backgroundWidth) + (columns * 18) && mouseY >= (top + this.backgroundHeight) - 90 && mouseY < (top + this.backgroundHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}
		}
	}
}

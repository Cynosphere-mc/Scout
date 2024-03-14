package pm.c7.scout.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.c7.scout.ScoutMixin;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.screen.BagSlot;

@Environment(EnvType.CLIENT)
@ScoutMixin.Transformer(HandledScreenTransformer.class)
@Mixin(value = HandledScreen.class, priority = 950)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
	protected HandledScreenMixin() {
		super(null);
	}

	@Shadow
	@Nullable
	protected Slot focusedSlot;
	@Shadow
	protected int x;
	@Shadow
	protected int y;
	@Shadow
	protected int backgroundWidth;
	@Shadow
	protected int backgroundHeight;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V"))
	private void scout$drawSatchelRow(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.client != null && this.client.player != null && !ScoutUtil.isScreenBlacklisted(this)) {
			ItemStack backStack = ScoutUtil.findBagItem(this.client.player, BaseBagItem.BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
				int slots = bagItem.getSlotCount();

				graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

				int x = this.x;
				int y = this.y + this.backgroundHeight - 3;

				if ((Object) this instanceof GenericContainerScreen || (Object) this instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 32, 176, 4);
				y += 4;

				int u = 0;
				int v = 36;

				for (int slot = 0; slot < slots; slot++) {
					if (slot % 9 == 0) {
						x = this.x;
						u = 0;
						graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18);
						x += 7;
						u += 7;
					}

					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 18, 18);

					x += 18;
					u += 18;

					if ((slot + 1) % 9 == 0) {
						graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18);
						y += 18;
					}
				}

				x = this.x;
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 54, 176, 7);

				graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", remap = false))
	private void scout$drawPouchSlots(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.client != null && this.client.player != null && !ScoutUtil.isScreenBlacklisted(this)) {
			ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BaseBagItem.BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				int x = this.x;
				int y = (this.y + this.backgroundHeight) - 29;

				if ((Object) this instanceof GenericContainerScreen || (Object) this instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 18, 25, 7, 7);
				for (int i = 0; i < columns; i++) {
					x -= 11;
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7);
				}
				if (columns > 1) {
					for (int i = 0; i < columns - 1; i++) {
						x -= 7;
						graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7);
					}
				}
				x -= 7;
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 25, 7, 7);

				x = this.x + 7;
				y -= 54;
				for (int slot = 0; slot < slots; slot++) {
					if (slot % 3 == 0) {
						x -= 18;
						y += 54;
					}
					y -= 18;
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18);
				}

				x -= 7;
				y += 54;
				for (int i = 0; i < 3; i++) {
					y -= 18;
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 7, 7, 18);
				}

				x = this.x;
				y -= 7;
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 18, 0, 7, 7);
				for (int i = 0; i < columns; i++) {
					x -= 11;
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7);
				}
				if (columns > 1) {
					for (int i = 0; i < columns - 1; i++) {
						x -= 7;
						graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7);
					}
				}
				x -= 7;
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 0, 7, 7);

				graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(this.client.player, BaseBagItem.BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				int x = this.x + this.backgroundWidth - 7;
				int y = (this.y + this.backgroundHeight) - 29;

				if ((Object) this instanceof GenericContainerScreen || (Object) this instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 25, 25, 7, 7);
				x += 7;
				for (int i = 0; i < columns; i++) {
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7);
					x += 11;
				}
				if (columns > 1) {
					for (int i = 0; i < columns - 1; i++) {
						graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7);
						x += 7;
					}
				}
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 32, 25, 7, 7);

				x = this.x + this.backgroundWidth - 25;
				y -= 54;
				for (int slot = 0; slot < slots; slot++) {
					if (slot % 3 == 0) {
						x += 18;
						y += 54;
					}
					y -= 18;
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18);
				}

				x += 18;
				y += 54;
				for (int i = 0; i < 3; i++) {
					y -= 18;
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 32, 7, 7, 18);
				}

				x = this.x + this.backgroundWidth - 7;
				y -= 7;
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 25, 0, 7, 7);
				x += 7;
				for (int i = 0; i < columns; i++) {
					graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7);
					x += 11;
				}
				if (columns > 1) {
					for (int i = 0; i < columns - 1; i++) {
						graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7);
						x += 7;
					}
				}
				graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 32, 0, 7, 7);

				graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
	}

	@Inject(method = "isClickOutsideBounds", at = @At("TAIL"), cancellable = true)
	private void scout$adjustOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (this.client != null && this.client.player != null && !ScoutUtil.isScreenBlacklisted(this)) {
			ItemStack backStack = ScoutUtil.findBagItem(this.client.player, BaseBagItem.BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				if (mouseY < (top + this.backgroundHeight) + 8 + (18 * rows) && mouseY >= (top + this.backgroundHeight) && mouseX >= left && mouseY < (left + this.backgroundWidth)) {
					callbackInfo.setReturnValue(false);
				}
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BaseBagItem.BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= left - (columns * 18) && mouseX < left && mouseY >= (top + this.backgroundHeight) - 90 && mouseY < (top + this.backgroundHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(this.client.player, BaseBagItem.BagType.POUCH, true);
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

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V"))
	public void scout$drawOurSlots(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.client != null && this.client.player != null && !ScoutUtil.isScreenBlacklisted(this)) {
			for (int i = ScoutUtil.SATCHEL_SLOT_START; i > ScoutUtil.BAG_SLOTS_END; i--) {
				BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(i, this.client.player.playerScreenHandler);
				if (slot != null && slot.isEnabled()) {
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					this.drawSlot(graphics, slot);
				}

				if (this.isPointOverSlot(slot, mouseX, mouseY) && slot != null && slot.isEnabled()) {
					this.focusedSlot = slot;
					int slotX = slot.getX();
					int slotY = slot.getY();
					drawSlotHighlight(graphics, slotX, slotY, 0);
				}
			}
		}
	}

	@Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
	public void scout$fixSlotPos(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if (slot instanceof BagSlot bagSlot) {
			cir.setReturnValue(this.isPointWithinBounds(bagSlot.getX(), bagSlot.getY(), 16, 16, pointX, pointY));
		}
	}

	@Inject(method = "getSlotAt", at = @At("RETURN"), cancellable = true)
	public void scout$addSlots(double x, double y, CallbackInfoReturnable<Slot> cir) {
		if (this.client != null && this.client.player != null && !ScoutUtil.isScreenBlacklisted(this)) {
			for (int i = ScoutUtil.SATCHEL_SLOT_START; i > ScoutUtil.BAG_SLOTS_END; i--) {
				BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(i, this.client.player.playerScreenHandler);
				if (this.isPointOverSlot(slot, x, y) && slot != null && slot.isEnabled()) {
					cir.setReturnValue(slot);
				}
			}
		}
	}

	@Shadow
	private void drawSlot(GuiGraphics graphics, Slot slot) {}
	@Shadow
	public static void drawSlotHighlight(GuiGraphics graphics, int x, int y, int z) {}
	@Shadow
	private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
		return false;
	}
	@Shadow
	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return false;
	}
}

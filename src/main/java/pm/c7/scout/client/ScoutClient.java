package pm.c7.scout.client;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.quiltmc.qsl.screen.api.client.ScreenEvents;
import org.quiltmc.qsl.tooltip.api.client.TooltipComponentCallback;
import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.ScoutScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.gui.BagTooltipComponent;
import pm.c7.scout.client.render.SatchelFeatureRenderer;
import pm.c7.scout.item.BagTooltipData;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.client.render.PouchFeatureRenderer;
import pm.c7.scout.mixin.client.HandledScreenAccessor;
import pm.c7.scout.screen.BagSlot;

public class ScoutClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ClientPlayNetworking.registerGlobalReceiver(ScoutNetworking.ENABLE_SLOTS, (client, handler, packet, sender) -> {
			client.execute(() -> {
				assert client.player != null;
				ScoutScreenHandler screenHandler = (ScoutScreenHandler) client.player.playerScreenHandler;

				ItemStack satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
				DefaultedList<BagSlot> satchelSlots = screenHandler.scout$getSatchelSlots();

				for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
					BagSlot slot = satchelSlots.get(i);
					slot.setInventory(null);
					slot.setEnabled(false);
				}
				if (!satchelStack.isEmpty()) {
					BaseBagItem satchelItem = (BaseBagItem) satchelStack.getItem();
					Inventory satchelInv = satchelItem.getInventory(satchelStack);

					for (int i = 0; i < satchelItem.getSlotCount(); i++) {
						BagSlot slot = satchelSlots.get(i);
						slot.setInventory(satchelInv);
						slot.setEnabled(true);
					}
				}

				ItemStack leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
				DefaultedList<BagSlot> leftPouchSlots = screenHandler.scout$getLeftPouchSlots();

				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					BagSlot slot = leftPouchSlots.get(i);
					slot.setInventory(null);
					slot.setEnabled(false);
				}
				if (!leftPouchStack.isEmpty()) {
					BaseBagItem leftPouchItem = (BaseBagItem) leftPouchStack.getItem();
					Inventory leftPouchInv = leftPouchItem.getInventory(leftPouchStack);

					for (int i = 0; i < leftPouchItem.getSlotCount(); i++) {
						BagSlot slot = leftPouchSlots.get(i);
						slot.setInventory(leftPouchInv);
						slot.setEnabled(true);
					}
				}

				ItemStack rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
				DefaultedList<BagSlot> rightPouchSlots = screenHandler.scout$getRightPouchSlots();

				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					BagSlot slot = rightPouchSlots.get(i);
					slot.setInventory(null);
					slot.setEnabled(false);
				}
				if (!rightPouchStack.isEmpty()) {
					BaseBagItem rightPouchItem = (BaseBagItem) rightPouchStack.getItem();
					Inventory rightPouchInv = rightPouchItem.getInventory(rightPouchStack);

					for (int i = 0; i < rightPouchItem.getSlotCount(); i++) {
						BagSlot slot = rightPouchSlots.get(i);
						slot.setInventory(rightPouchInv);
						slot.setEnabled(true);
					}
				}
			});
		});

		TooltipComponentCallback.EVENT.register(data -> {
			if (data instanceof BagTooltipData d) {
				return new BagTooltipComponent(d);
			}

			return null;
		});

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityType == EntityType.PLAYER) {
				registrationHelper.register(new PouchFeatureRenderer<>(entityRenderer, context.getHeldItemRenderer()));
				registrationHelper.register(new SatchelFeatureRenderer<>(entityRenderer));
			}
		});

		ScreenEvents.AFTER_INIT.register((screen, client, firstInit) -> {
			if (screen instanceof HandledScreen<?> handledScreen && client.player != null) {
				if (ScoutUtil.isScreenBlacklisted(screen)) {
					// realistically no one is going to have a screen bigger than 2147483647 pixels
					for (Slot slot : ScoutUtil.getAllBagSlots(client.player.playerScreenHandler)) {
						BagSlot bagSlot = (BagSlot) slot;
						bagSlot.setX(Integer.MAX_VALUE);
						bagSlot.setY(Integer.MAX_VALUE);
					}
					return;
				}

				var handledScreenAccessor = (HandledScreenAccessor) handledScreen;

				var sx = handledScreenAccessor.getX();
				var sy = handledScreenAccessor.getY();
				var sw = handledScreenAccessor.getBackgroundWidth();
				var sh = handledScreenAccessor.getBackgroundHeight();

				// satchel
				int x = sx;
				int y = sy + sh + 2;

				if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
					if (i % 9 == 0) {
						x = sx + 8;
					}

					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, client.player.playerScreenHandler);
					if (slot != null) {
						slot.setX(x - sx);
						slot.setY(y - sy);
					}

					x += 18;

					if ((i + 1) % 9 == 0) {
						y += 18;
					}
				}

				// left pouch
				x = sx + 8;
				y = (sy + sh) - 100;

				if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					if (i % 3 == 0) {
						x -= 18;
						y += 54;
					}

					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, client.player.playerScreenHandler);
					if (slot != null) {
						slot.setX(x - sx);
						slot.setY(y - sy);
					}

					y -= 18;
				}

				// right pouch
				x = sx + sw - 24;
				y = (sy + sh) - 100;

				if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					if (i % 3 == 0) {
						x += 18;
						y += 54;
					}

					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, client.player.playerScreenHandler);
					if (slot != null) {
						slot.setX(x - sx);
						slot.setY(y - sy);
					}

					y -= 18;
				}
			}
		});
	}
}

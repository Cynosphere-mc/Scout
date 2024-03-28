package pm.c7.scout.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

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
	public void onInitializeClient() {
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

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof HandledScreen<?> handledScreen && client.player != null) {
				if (ScoutUtilClient.isScreenBlacklisted(screen)) {
					// realistically no one is going to have a screen bigger than 2147483647 pixels
					for (Slot slot : ScoutUtil.getAllBagSlots(client.player.playerScreenHandler)) {
						BagSlot bagSlot = (BagSlot) slot;
						bagSlot.setX(Integer.MAX_VALUE);
						bagSlot.setY(Integer.MAX_VALUE);
					}
					return;
				}

				var handledScreenAccessor = (HandledScreenAccessor<?>) handledScreen;
				ScreenHandler handler = handledScreenAccessor.getHandler();

				var playerInventory = client.player.getInventory();

				int x = 0;
				int y = 0;

				// satchel
				var _hotbarSlot1 = handler.slots.stream().filter(slot->slot.inventory.equals(playerInventory) && slot.getIndex() == 0).findFirst();
				Slot hotbarSlot1 = _hotbarSlot1.isPresent() ? _hotbarSlot1.get() : null;
				if (hotbarSlot1 != null) {
					if (!hotbarSlot1.isEnabled()) {
						for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
							BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, client.player.playerScreenHandler);
							if (slot != null) {
								slot.setX(Integer.MAX_VALUE);
								slot.setY(Integer.MAX_VALUE);
							}
						}
					} else {
						x = hotbarSlot1.x;
						y = hotbarSlot1.y + 27;

						for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
							if (i % 9 == 0) {
								x = hotbarSlot1.x;
							}

							BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, client.player.playerScreenHandler);
							if (slot != null) {
								slot.setX(x);
								slot.setY(y);
							}

							x += 18;

							if ((i + 1) % 9 == 0) {
								y += 18;
							}
						}
					}
				}

				// left pouch
				var _topLeftSlot = handler.slots.stream().filter(slot->slot.inventory.equals(playerInventory) && slot.getIndex() == 9).findFirst();
				Slot topLeftSlot = _topLeftSlot.isPresent() ? _topLeftSlot.get() : null;
				if (topLeftSlot != null) {
					if (!topLeftSlot.isEnabled()) {
						for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
							BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, client.player.playerScreenHandler);
							if (slot != null) {
								slot.setX(Integer.MAX_VALUE);
								slot.setY(Integer.MAX_VALUE);
							}
						}
					} else {
						x = topLeftSlot.x;
						y = topLeftSlot.y - 18;

						for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
							if (i % 3 == 0) {
								x -= 18;
								y += 54;
							}

							BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, client.player.playerScreenHandler);
							if (slot != null) {
								slot.setX(x);
								slot.setY(y);
							}

							y -= 18;
						}
					}
				}

				// right pouch
				var _topRightSlot = handler.slots.stream().filter(slot->slot.inventory.equals(playerInventory) && slot.getIndex() == 17).findFirst();
				Slot topRightSlot = _topRightSlot.isPresent() ? _topRightSlot.get() : null;
				if (topRightSlot != null) {
					if (!topLeftSlot.isEnabled()) {
						for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
							BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, client.player.playerScreenHandler);
							if (slot != null) {
								slot.setX(Integer.MAX_VALUE);
								slot.setY(Integer.MAX_VALUE);
							}
						}
					} else {
						x = topRightSlot.x;
						y = topRightSlot.y - 18;

						for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
							if (i % 3 == 0) {
								x += 18;
								y += 54;
							}

							BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, client.player.playerScreenHandler);
							if (slot != null) {
								slot.setX(x);
								slot.setY(y);
							}

							y -= 18;
						}
					}
				}
			}
		});
	}
}

package pm.c7.scout.client.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.mixin.client.HandledScreenAccessor;

public class ScoutEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addGenericExclusionArea((screen, consumer) -> {
			if (!(screen instanceof HandledScreen<?> handledScreen)) return;
			if (ScoutUtilClient.isScreenBlacklisted(screen)) return;

			MinecraftClient client = MinecraftClient.getInstance();

			var handledScreenAccessor = (HandledScreenAccessor<?>) handledScreen;
			ScreenHandler handler = handledScreenAccessor.getHandler();
			var sx = handledScreenAccessor.getX();
			var sy = handledScreenAccessor.getY();
			var sw = handledScreenAccessor.getBackgroundWidth();
			var sh = handledScreenAccessor.getBackgroundHeight();

			var playerInventory = client.player.getInventory();

			ItemStack satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
			if (!satchelStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) satchelStack.getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				var _hotbarSlot1 = handler.slots.stream().filter(slot->slot.inventory.equals(playerInventory) && slot.getIndex() == 0).findFirst();
				Slot hotbarSlot1 = _hotbarSlot1.isPresent() ? _hotbarSlot1.get() : null;
				if (hotbarSlot1 != null) {
					if (hotbarSlot1.isEnabled()) {
						int x = sx + hotbarSlot1.x - 8;
						int y = sy + hotbarSlot1.y + 22;

						int w = 176;
						int h = (rows * 18) + 8;

						consumer.accept(new Bounds(x, y, w, h));
					}
				}
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				var _topLeftSlot = handler.slots.stream().filter(slot->slot.inventory.equals(playerInventory) && slot.getIndex() == 9).findFirst();
				Slot topLeftSlot = _topLeftSlot.isPresent() ? _topLeftSlot.get() : null;
				if (topLeftSlot != null) {
					if (topLeftSlot.isEnabled()) {
						int x = sx + topLeftSlot.x - 7 - (columns * 18);
						int y = sy + topLeftSlot.y;

						int w = (columns * 18) + 7;
						int h = 68;

						consumer.accept(new Bounds(x, y, w, h));
					}
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				var _topRightSlot = handler.slots.stream().filter(slot->slot.inventory.equals(playerInventory) && slot.getIndex() == 17).findFirst();
				Slot topRightSlot = _topRightSlot.isPresent() ? _topRightSlot.get() : null;
				if (topRightSlot != null) {
					if (topRightSlot.isEnabled()) {
						int x = sx + topRightSlot.x;
						int y = sy + topRightSlot.y;

						int w = (columns * 18) + 7;
						int h = 68;

						consumer.accept(new Bounds(x, y, w, h));
					}
				}
			}
		});
	}

}

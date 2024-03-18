package pm.c7.scout.client.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.ItemStack;
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

			var handledScreenAccessor = (HandledScreenAccessor) handledScreen;

			var sx = handledScreenAccessor.getX();
			var sy = handledScreenAccessor.getY();
			var sw = handledScreenAccessor.getBackgroundWidth();
			var sh = handledScreenAccessor.getBackgroundHeight();

			ItemStack satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
			if (!satchelStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) satchelStack.getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				int x = sx;
				int y = sy + sh;

				if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				int w = sw;
				int h = (rows * 18) + 8;

				consumer.accept(new Bounds(x, y, w, h));
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				int x = sx - (columns * 18);
				int y = (sy + sh) - 100;

				if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				int w = (columns * 18);
				int h = 68;

				consumer.accept(new Bounds(x, y, w, h));
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				int x = sx + sw;
				int y = (sy + sh) - 100;

				if (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen) {
					y -= 1;
				}

				int w = (columns * 18);
				int h = 68;

				consumer.accept(new Bounds(x, y, w, h));
			}
		});
	}

}

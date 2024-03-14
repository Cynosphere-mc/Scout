package pm.c7.scout.client.gui;

import com.google.common.math.IntMath;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BagTooltipData;

import java.math.RoundingMode;

public class BagTooltipComponent implements TooltipComponent {
	private final DefaultedList<ItemStack> inventory;
	private final int slotCount;

	public BagTooltipComponent(BagTooltipData data) {
		this.inventory = data.getInventory();
		this.slotCount = data.getSlotCount();
	}

	@Override
	public int getHeight() {
		return (18 * IntMath.divide(slotCount, 6, RoundingMode.UP)) + 2;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 18 * (Math.min(slotCount, 6));
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, GuiGraphics graphics) {
		int originalX = x;

		for (int i = 0; i < slotCount; i++) {
			this.drawSlot(x, y, i, graphics, textRenderer);

			x += 18;
			if ((i + 1) % 6 == 0) {
				y += 18;
				x = originalX;
			}
		}
	}

	private void drawSlot(int x, int y, int index, GuiGraphics graphics, TextRenderer textRenderer) {
		ItemStack itemStack = this.inventory.get(index);
		graphics.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 46, 7, 18, 18, 256, 256);
		graphics.drawItem(itemStack, x + 1, y + 1, index);
		graphics.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
	}
}

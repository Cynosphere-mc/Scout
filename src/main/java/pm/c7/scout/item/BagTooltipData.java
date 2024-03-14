package pm.c7.scout.item;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BagTooltipData implements TooltipData {
    private final DefaultedList<ItemStack> inventory;
    private final int slotCount;

    public BagTooltipData(DefaultedList<ItemStack> inventory, int slots) {
        this.inventory = inventory;
        this.slotCount = slots;
    }

    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    public int getSlotCount() {
        return this.slotCount;
    }
}

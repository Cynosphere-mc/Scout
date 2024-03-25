package pm.c7.scout.screen;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.item.BaseBagItem;

public class BagSlot extends Slot {
	private final int index;
	public Inventory inventory;
	private boolean enabled = false;
	private int realX;
	private int realY;

	public BagSlot(int index, int x, int y) {
		super(null, index, x, y);
		this.index = index;
		this.realX = x;
		this.realY = y;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public void setEnabled(boolean state) {
		enabled = state;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		if (stack.getItem() instanceof BaseBagItem)
			return false;

		if (stack.isIn(ScoutUtil.TAG_ITEM_BLACKLIST)) {
			return false;
		}

		if (stack.getItem() instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof ShulkerBoxBlock)
				return enabled && inventory != null && ScoutConfig.allowShulkers;
		}

		return enabled && inventory != null;
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity) {
		return enabled && inventory != null;
	}

	@Override
	public boolean isEnabled() {
		return enabled && inventory != null;
	}

	@Override
	public ItemStack getStack() {
		return enabled && this.inventory != null ? this.inventory.getStack(this.index) : ItemStack.EMPTY;
	}

	@Override
	public void setStackNoCallbacks(ItemStack stack) {
		if (enabled && this.inventory != null) {
			this.inventory.setStack(this.index, stack);
			this.markDirty();
		}
	}

	@Override
	public void markDirty() {
		if (enabled && this.inventory != null) {
			this.inventory.markDirty();
		}
	}

	@Override
	public ItemStack takeStack(int amount) {
		return enabled && this.inventory != null ? this.inventory.removeStack(this.index, amount) : ItemStack.EMPTY;
	}

	@Override
	public int getMaxItemCount() {
		return enabled && this.inventory != null ? this.inventory.getMaxCountPerStack() : 0;
	}

	public int getX() {
		return this.realX;
	}
	public int getY() {
		return this.realY;
	}
	public void setX(int x) {
		this.realX = x;
	}
	public void setY(int y) {
		this.realY = y;
	}
}

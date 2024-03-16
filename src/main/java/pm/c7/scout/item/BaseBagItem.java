package pm.c7.scout.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.ScoutScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.screen.BagSlot;

import java.util.List;
import java.util.Optional;

public class BaseBagItem extends TrinketItem {
	private static final String ITEMS_KEY = "Items";

	private final int slots;
	private final BagType type;

	public BaseBagItem(Settings settings, int slots, BagType type) {
		super(settings);

		if (type == BagType.SATCHEL && slots > ScoutUtil.MAX_SATCHEL_SLOTS) {
			throw new IllegalArgumentException("Satchel has too many slots.");
		}
		if (type == BagType.POUCH && slots > ScoutUtil.MAX_POUCH_SLOTS) {
			throw new IllegalArgumentException("Pouch has too many slots.");
		}

		this.slots = slots;
		this.type = type;
	}

	public int getSlotCount() {
		return this.slots;
	}

	public BagType getType() {
		return this.type;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("tooltip.scout.slots", Text.literal(String.valueOf(this.slots)).formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
	}

	public Inventory getInventory(ItemStack stack) {
		SimpleInventory inventory = new SimpleInventory(this.slots) {
			@Override
			public void markDirty() {
				stack.getOrCreateNbt().put(ITEMS_KEY, ScoutUtil.inventoryToTag(this));
				super.markDirty();
			}
		};

		NbtCompound compound = stack.getOrCreateNbt();
		if (!compound.contains(ITEMS_KEY)) {
			compound.put(ITEMS_KEY, new NbtList());
		}

		NbtList items = compound.getList(ITEMS_KEY, 10);

		ScoutUtil.inventoryFromTag(items, inventory);

		return inventory;
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		DefaultedList<ItemStack> stacks = DefaultedList.of();
		Inventory inventory = getInventory(stack);

		for (int i = 0; i < slots; i++) {
			stacks.add(inventory.getStack(i));
		}

		if (stacks.stream().allMatch(ItemStack::isEmpty)) return Optional.empty();

		return Optional.of(new BagTooltipData(stacks, slots));
	}

	@Override
	public void onEquip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
		if (entity instanceof PlayerEntity player)
			updateSlots(player);
	}

	@Override
	public void onUnequip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
		if (entity instanceof PlayerEntity player)
			updateSlots(player);
	}

	private void updateSlots(PlayerEntity player) {
		ScoutScreenHandler handler = (ScoutScreenHandler) player.playerScreenHandler;

		ItemStack satchelStack = ScoutUtil.findBagItem(player, BagType.SATCHEL, false);
		DefaultedList<BagSlot> satchelSlots = handler.scout$getSatchelSlots();

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

		ItemStack leftPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, false);
		DefaultedList<BagSlot> leftPouchSlots = handler.scout$getLeftPouchSlots();

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

		ItemStack rightPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, true);
		DefaultedList<BagSlot> rightPouchSlots = handler.scout$getRightPouchSlots();

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

		PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
		if (player instanceof ServerPlayerEntity serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, ScoutNetworking.ENABLE_SLOTS, packet);
		}
	}

	@Override
	public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
		Item item = stack.getItem();

		ItemStack slotStack = slot.inventory().getStack(slot.index());
		Item slotItem = slotStack.getItem();

		if (slotItem instanceof BaseBagItem) {
			if (((BaseBagItem) item).getType() == BagType.SATCHEL) {
				if (((BaseBagItem) slotItem).getType() == BagType.SATCHEL) {
					return true;
				} else {
					return ScoutUtil.findBagItem((PlayerEntity) entity, BagType.SATCHEL, false).isEmpty();
				}
			} else if (((BaseBagItem) item).getType() == BagType.POUCH) {
				if (((BaseBagItem) slotItem).getType() == BagType.POUCH) {
					return true;
				} else {
					return ScoutUtil.findBagItem((PlayerEntity) entity, BagType.POUCH, true).isEmpty();
				}
			}
		} else {
			if (((BaseBagItem) item).getType() == BagType.SATCHEL) {
				return ScoutUtil.findBagItem((PlayerEntity) entity, BagType.SATCHEL, false).isEmpty();
			} else if (((BaseBagItem) item).getType() == BagType.POUCH) {
				return ScoutUtil.findBagItem((PlayerEntity) entity, BagType.POUCH, true).isEmpty();
			}
		}

		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		var inv = getInventory(stack);

		for (int i = 0; i < inv.size(); i++) {
			var invStack = inv.getStack(i);
			invStack.inventoryTick(world, entity, i, false);
		}
	}

	public enum BagType {
		SATCHEL,
		POUCH
	}
}

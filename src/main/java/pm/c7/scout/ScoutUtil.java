package pm.c7.scout;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

import java.util.Optional;

public class ScoutUtil {
	public static final Logger LOGGER = LoggerFactory.getLogger("Scout");
	public static final String MOD_ID = "scout";
	public static final Identifier SLOT_TEXTURE = new Identifier(MOD_ID, "textures/gui/slots.png");

	public static final TagKey<Item> TAG_ITEM_BLACKLIST = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "blacklist"));

	public static final int MAX_SATCHEL_SLOTS = 18;
	public static final int MAX_POUCH_SLOTS = 6;
	public static final int TOTAL_SLOTS = MAX_SATCHEL_SLOTS + MAX_POUCH_SLOTS + MAX_POUCH_SLOTS;

	public static final int SATCHEL_SLOT_START = -1100;
	public static final int LEFT_POUCH_SLOT_START = SATCHEL_SLOT_START - MAX_SATCHEL_SLOTS;
	public static final int RIGHT_POUCH_SLOT_START = LEFT_POUCH_SLOT_START - MAX_POUCH_SLOTS;
	public static final int BAG_SLOTS_END = RIGHT_POUCH_SLOT_START - MAX_POUCH_SLOTS;

	public static ItemStack findBagItem(PlayerEntity player, BaseBagItem.BagType type, boolean right) {
		ItemStack targetStack = ItemStack.EMPTY;

		boolean hasFirstPouch = false;
		Optional<TrinketComponent> _component = TrinketsApi.getTrinketComponent(player);
		if (_component.isPresent()) {
			TrinketComponent component = _component.get();
			for (Pair<SlotReference, ItemStack> pair : component.getAllEquipped()) {
				ItemStack slotStack = pair.getRight();

				if (slotStack.getItem() instanceof BaseBagItem bagItem) {
					if (bagItem.getType() == type) {
						if (type == BagType.POUCH) {
							if (right && !hasFirstPouch) {
								hasFirstPouch = true;
							} else {
								targetStack = slotStack;
								break;
							}
						} else {
							targetStack = slotStack;
							break;
						}
					}
				}
			}
		}

		return targetStack;
	}

	public static NbtList inventoryToTag(Inventory inventory) {
		NbtList tag = new NbtList();

		for(int i = 0; i < inventory.size(); i++) {
			NbtCompound stackTag = new NbtCompound();
			stackTag.putInt("Slot", i);
			stackTag.put("Stack", inventory.getStack(i).writeNbt(new NbtCompound()));
			tag.add(stackTag);
		}

		return tag;
	}

	public static void inventoryFromTag(NbtList tag, Inventory inventory) {
		inventory.clear();

		tag.forEach(element -> {
			NbtCompound stackTag = (NbtCompound) element;
			int slot = stackTag.getInt("Slot");
			ItemStack stack = ItemStack.fromNbt(stackTag.getCompound("Stack"));
			inventory.setStack(slot, stack);
		});
	}

	public static boolean isBagSlot(int slot) {
		return slot <= SATCHEL_SLOT_START && slot > BAG_SLOTS_END;
	}

	public static @Nullable Slot getBagSlot(int slot, PlayerScreenHandler playerScreenHandler) {
		var scoutScreenHandler = (ScoutScreenHandler) playerScreenHandler;
		if (slot <= SATCHEL_SLOT_START && slot > LEFT_POUCH_SLOT_START) {
			int realSlot = MathHelper.abs(slot - SATCHEL_SLOT_START);
			var slots = scoutScreenHandler.scout$getSatchelSlots();

			return slots.get(realSlot);
		} else if (slot <= LEFT_POUCH_SLOT_START && slot > RIGHT_POUCH_SLOT_START) {
			int realSlot = MathHelper.abs(slot - LEFT_POUCH_SLOT_START);
			var slots = scoutScreenHandler.scout$getLeftPouchSlots();

			return slots.get(realSlot);
		} else if (slot <= RIGHT_POUCH_SLOT_START && slot > BAG_SLOTS_END) {
			int realSlot = MathHelper.abs(slot - RIGHT_POUCH_SLOT_START);
			var slots = scoutScreenHandler.scout$getRightPouchSlots();

			return slots.get(realSlot);
		} else {
			return null;
		}
	}

	public static DefaultedList<Slot> getAllBagSlots(PlayerScreenHandler playerScreenHandler) {
		var scoutScreenHandler = (ScoutScreenHandler) playerScreenHandler;
		DefaultedList<Slot> out = DefaultedList.ofSize(TOTAL_SLOTS);
		out.addAll(scoutScreenHandler.scout$getSatchelSlots());
		out.addAll(scoutScreenHandler.scout$getLeftPouchSlots());
		out.addAll(scoutScreenHandler.scout$getRightPouchSlots());
		return out;
	}
}

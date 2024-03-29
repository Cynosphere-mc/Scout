package pm.c7.scout.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Rarity;
import pm.c7.scout.Scout;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;

public class ScoutItems {
	public static final Item TANNED_LEATHER = new Item(new FabricItemSettings());
	public static final Item SATCHEL_STRAP = new Item(new FabricItemSettings());
	public static final BaseBagItem SATCHEL = new BaseBagItem(new FabricItemSettings().maxCount(1), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL);
	public static final BaseBagItem UPGRADED_SATCHEL = new BaseBagItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL);
	public static final BaseBagItem POUCH = new BaseBagItem(new FabricItemSettings().maxCount(1), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH);
	public static final BaseBagItem UPGRADED_POUCH = new BaseBagItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH);

	public static void init() {
		Scout.AUTOREGISTRY.autoRegister(Registries.ITEM, ScoutItems.class, Item.class);
	}
}

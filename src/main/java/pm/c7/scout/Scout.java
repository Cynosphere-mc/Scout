package pm.c7.scout;

import com.unascribed.lib39.core.api.AutoRegistry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import pm.c7.scout.config.ScoutConfigHandler;
import pm.c7.scout.registry.ScoutItems;

public class Scout implements ModInitializer {
	public static final AutoRegistry AUTOREGISTRY = AutoRegistry.of(ScoutUtil.MOD_ID);
	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ScoutItems.SATCHEL))
		.name(Text.translatable("itemGroup.scout.itemgroup"))
		.entries((context, entries) -> {
			entries.addItem(ScoutItems.TANNED_LEATHER);
			entries.addItem(ScoutItems.SATCHEL_STRAP);
			entries.addItem(ScoutItems.SATCHEL);
			entries.addItem(ScoutItems.UPGRADED_SATCHEL);
			entries.addItem(ScoutItems.POUCH);
			entries.addItem(ScoutItems.UPGRADED_POUCH);
		})
		.build();

	@Override
	public void onInitialize(ModContainer mod) {
		new ScoutConfigHandler();
		ScoutItems.init();
		Registry.register(Registries.ITEM_GROUP, new Identifier(ScoutUtil.MOD_ID, "itemgroup"), ITEM_GROUP);
	}
}

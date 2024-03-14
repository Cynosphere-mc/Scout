package pm.c7.scout;

import com.unascribed.lib39.core.api.AutoRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import pm.c7.scout.config.ScoutConfigHandler;
import pm.c7.scout.registry.ScoutItems;

public class Scout implements ModInitializer {
	public static final AutoRegistry AUTOREGISTRY = AutoRegistry.of(ScoutUtil.MOD_ID);
    public static final ItemGroup ITEM_GROUP = QuiltItemGroup.createWithIcon(new Identifier("scout", "itemgroup"), () -> new ItemStack(ScoutItems.SATCHEL));

	@Override
    public void onInitialize(ModContainer mod) {
		new ScoutConfigHandler();
        ScoutItems.init();
    }
}

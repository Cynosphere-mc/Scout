package pm.c7.scout;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class ScoutConfig extends ReflectiveConfig {
	public static final ScoutConfig CONFIG = QuiltConfig.create("", ScoutUtil.MOD_ID, ScoutConfig.class);

	@Comment("Allow shulker boxes to be placed in bags. Bags are already blacklisted from shulker boxes with no toggle.")
	public final TrackedValue<Boolean> allowShulkers = this.value(true);

	@Comment("Allow bags to act as a quiver and pull arrows.")
	public final TrackedValue<Boolean> useArrows = this.value(true);
}

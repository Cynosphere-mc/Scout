package pm.c7.scout.config;

import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;

public class ScoutConfig extends WrappedConfig {
	@Comment("Allow shulker boxes to be placed in bags. Bags are already blacklisted from shulker boxes with no toggle.")
	public final boolean allowShulkers = true;

	@Comment("Allow bags to act as a quiver and pull arrows.")
	public final boolean useArrows = true;
}

package pm.c7.scout.config;

import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.QuiltConfig;
import pm.c7.scout.ScoutUtil;

import java.util.List;

public class ScoutConfigHandler {
	public static final ScoutConfig CONFIG = QuiltConfig.create("", ScoutUtil.MOD_ID, ScoutConfig.class);

	public ScoutConfigHandler() {}

	public static TrackedValue<?> getConfigValue(String key) {
		return CONFIG.getValue(List.of(key));
	}
}

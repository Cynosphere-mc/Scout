package pm.c7.scout.client;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.PlayerScreenHandler;

public class ScoutUtilClient {
	public static @Nullable PlayerScreenHandler getPlayerScreenHandler() {
		var client = MinecraftClient.getInstance();
		if (client != null && client.player != null) {
			return client.player.playerScreenHandler;
		}

		return null;
	}

	// FIXME: registry system for mods to register their own blacklisted screens
	public static boolean isScreenBlacklisted(Screen screen) {
		return screen instanceof CreativeInventoryScreen;
	}
}

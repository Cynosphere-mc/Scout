package pm.c7.scout.server;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import pm.c7.scout.ScoutUtil;

public class ScoutUtilServer {
	private static PlayerEntity currentPlayer = null;

	public static void setCurrentPlayer(PlayerEntity player) {
		if (currentPlayer != null) {
			ScoutUtil.LOGGER.warn("[Scout] New player set during existing quick move, expect players getting wrong items!");
		}
		currentPlayer = player;
	}

	public static void clearCurrentPlayer() {
		currentPlayer = null;
	}

	public static @Nullable PlayerEntity getCurrentPlayer() {
		return currentPlayer;
	}
}

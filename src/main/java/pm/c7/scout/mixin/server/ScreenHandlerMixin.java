package pm.c7.scout.mixin.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.c7.scout.server.ScoutUtilServer;

@Environment(EnvType.SERVER)
@Mixin(value = ScreenHandler.class)
public abstract class ScreenHandlerMixin {
	@Redirect(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"))
	public ItemStack scout$fixQuickMove(ScreenHandler self, PlayerEntity player, int index, int slotIndex, int button, SlotActionType actionType, PlayerEntity playerAgain) {
		ScoutUtilServer.setCurrentPlayer(player);
		ItemStack ret = self.quickMove(player, index);
		ScoutUtilServer.clearCurrentPlayer();

		return ret;
	}
}

package pm.c7.scout.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public interface HandledScreenAccessor<T extends ScreenHandler> {
	@Accessor("x")
	int getX();
	@Accessor("y")
	int getY();
	@Accessor("backgroundWidth")
	int getBackgroundWidth();
	@Accessor("backgroundHeight")
	int getBackgroundHeight();
	@Accessor("handler")
	T getHandler();
}

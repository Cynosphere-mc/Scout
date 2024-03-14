package pm.c7.scout.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.c7.scout.ScoutScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.ScoutMixin.Transformer;
import pm.c7.scout.screen.BagSlot;

@Mixin(value = PlayerScreenHandler.class, priority = 950)
@Transformer(PlayerScreenHandlerTransformer.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler implements ScoutScreenHandler {
    protected PlayerScreenHandlerMixin() {
        super(null, 0);
    }

    @Unique
    public final DefaultedList<BagSlot> scout$satchelSlots = DefaultedList.ofSize(ScoutUtil.MAX_SATCHEL_SLOTS);
    @Unique
    public final DefaultedList<BagSlot> scout$leftPouchSlots = DefaultedList.ofSize(ScoutUtil.MAX_POUCH_SLOTS);
    @Unique
    public final DefaultedList<BagSlot> scout$rightPouchSlots = DefaultedList.ofSize(ScoutUtil.MAX_POUCH_SLOTS);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void scout$addSlots(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo callbackInfo) {
        // satchel
        int x = 8;
        int y = 168;

        for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
            if (i % 9 == 0) {
                x = 8;
            }

            BagSlot slot = new BagSlot(i, x, y);
			slot.id = ScoutUtil.SATCHEL_SLOT_START - i;
			scout$satchelSlots.add(slot);

            x += 18;

            if ((i + 1) % 9 == 0) {
                y += 18;
            }
        }

        // left pouch
        x = 8;
        y = 66;

        for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
            if (i % 3 == 0) {
                x -= 18;
                y += 54;
            }

            BagSlot slot = new BagSlot(i, x, y);
			slot.id = ScoutUtil.LEFT_POUCH_SLOT_START - i;
			scout$leftPouchSlots.add(slot);

            y -= 18;
        }

        // right pouch
        x = 152;
        y = 66;

        for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
            if (i % 3 == 0) {
                x += 18;
                y += 54;
            }

            BagSlot slot = new BagSlot(i, x, y);
			slot.id = ScoutUtil.RIGHT_POUCH_SLOT_START - i;
			scout$rightPouchSlots.add(slot);

            y -= 18;
        }
    }

    @Override
    public final DefaultedList<BagSlot> scout$getSatchelSlots() {
        return scout$satchelSlots;
    }
    @Override
    public final DefaultedList<BagSlot> scout$getLeftPouchSlots() {
        return scout$leftPouchSlots;
    }
    @Override
    public final DefaultedList<BagSlot> scout$getRightPouchSlots() {
        return scout$rightPouchSlots;
    }
}

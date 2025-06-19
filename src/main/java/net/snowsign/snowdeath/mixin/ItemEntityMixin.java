package net.snowsign.snowdeath.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.snowsign.snowdeath.MarkedItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.snowsign.snowdeath.MixinUtil.getPlayerDeaths;

@Debug(export = true)
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements MarkedItem {
    @Unique
    private int deathCount = Short.MIN_VALUE;

    @Shadow
    public abstract @Nullable Entity getOwner();

    @Shadow private int itemAge;

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void writeMarkedData(WriteView view, CallbackInfo ci) {
        view.putShort("DeathCount", (short) this.deathCount);
    }

    @Inject(method = "readCustomData", at = @At("HEAD"))
    public void readMarkedData(ReadView view, CallbackInfo ci) {
        this.deathCount = view.getShort("DeathCount", (short) -1);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/entity/ItemEntity;discard()V"))
    public void discardIfNotMarked(ItemEntity instance) {
        Entity owner = instance.getOwner();

        if (
            owner instanceof ServerPlayerEntity &&
                getPlayerDeaths((ServerPlayerEntity) owner) - ((MarkedItem) instance).snowdeath$getDeathCount() < 5
        ) {
            this.itemAge = 6000; // Prevent overflow
            return;
        }
        instance.discard();
    }

    @Override
    public void snowdeath$mark(int deaths) {
        if (this.getOwner() instanceof ServerPlayerEntity) {
            this.deathCount = deaths;
        }
    }

    @Override
    public int snowdeath$getDeathCount() {
        return this.deathCount;
    }
}
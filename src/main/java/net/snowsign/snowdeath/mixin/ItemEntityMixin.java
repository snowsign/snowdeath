package net.snowsign.snowdeath.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeMarkedDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putShort("DeathCount", (short) this.deathCount);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.deathCount = nbt.getShort("DeathCount", (short) -1);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;discard()V"))
    public void discardIfNotMarked(ItemEntity instance) {
        Entity owner = instance.getOwner();

        if (
            owner instanceof ServerPlayerEntity &&
                getPlayerDeaths((ServerPlayerEntity) owner) - ((MarkedItem) instance).snowdeath$getDeathCount() < 5
        ) {
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
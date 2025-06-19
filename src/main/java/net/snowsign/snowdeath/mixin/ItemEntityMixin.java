package net.snowsign.snowdeath.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
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

import java.util.UUID;

import static net.snowsign.snowdeath.MixinUtil.getPlayerDeaths;

@Debug(export = true)
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements MarkedItem {
    @Unique
    private int deathCount = Short.MIN_VALUE;

    @Unique
    private @Nullable UUID deceased = null;
    @Shadow
    private int itemAge;


    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void writeMarkedData(WriteView view, CallbackInfo ci) {
        view.putShort("DeathCount", (short) this.deathCount);
        view.putNullable("Deceased", Uuids.INT_STREAM_CODEC, this.deceased);
    }

    @Inject(method = "readCustomData", at = @At("HEAD"))
    public void readMarkedData(ReadView view, CallbackInfo ci) {
        this.deathCount = view.getShort("DeathCount", (short) -1);
        this.deceased = view.read("Deceased", Uuids.INT_STREAM_CODEC).orElse(null);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/entity/ItemEntity;discard()V"))
    public void discardIfNotMarked(ItemEntity instance) {
        if (deceased == null) instance.discard();

        Integer deaths = getPlayerDeaths(instance.getServer(), deceased);
        if (
            deaths == null
                || deaths - ((MarkedItem) instance).snowdeath$getDeathCount() < 5
        ) {
            this.itemAge = 6000; // Prevent overflow
            return;
        }
        instance.discard();
    }

    @Override
    public void snowdeath$mark(UUID deceased, int deaths) {
        this.deceased = deceased;
        this.deathCount = deaths;
    }

    @Override
    public int snowdeath$getDeathCount() {
        return this.deathCount;
    }
}
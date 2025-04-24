package net.snowsign.snowdeath.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.snowsign.snowdeath.MarkedItem;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.snowsign.snowdeath.MixinUtil.getPlayerDeaths;

@Debug(export = true)
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    @Redirect(method = "dropAll()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
    private ItemEntity dropItemMarked(PlayerEntity instance, ItemStack itemStack, boolean dropAtSelf, boolean retainOwnership) {
        ItemEntity droppedItem = instance.dropItem(itemStack, true, true);
        if (droppedItem != null && this.player instanceof ServerPlayerEntity) {
            int deaths = getPlayerDeaths((ServerPlayerEntity) this.player) + 1;
            ((MarkedItem) droppedItem).snowdeath$mark(deaths);
        }
        return droppedItem;
    }
}
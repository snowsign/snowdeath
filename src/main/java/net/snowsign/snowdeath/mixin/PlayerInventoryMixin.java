package net.snowsign.snowdeath.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.snowsign.snowdeath.MarkedItem;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.snowsign.snowdeath.MixinUtil.getPlayerDeaths;

@Debug(export = true)
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Redirect(method = "dropAll()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"))
    private ItemEntity dropItemMarked(PlayerEntity instance, ItemStack itemStack, boolean dropAtSelf, boolean retainOwnership) {
        ItemEntity droppedItem = instance.dropItem(itemStack, true, false);
        if (
            droppedItem != null
                && instance instanceof ServerPlayerEntity serverPlayer
                && serverPlayer.getServer() != null
        ) {
            Integer deaths = getPlayerDeaths(serverPlayer.getServer(), serverPlayer.getUuid());
            ((MarkedItem) droppedItem).snowdeath$mark(serverPlayer.getUuid(), deaths != null ? deaths + 1 : 1);
        }
        return droppedItem;
    }
}
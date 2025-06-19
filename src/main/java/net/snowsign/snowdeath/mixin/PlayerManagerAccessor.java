package net.snowsign.snowdeath.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.stat.ServerStatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
public interface PlayerManagerAccessor {
    @Accessor
    Map<UUID, ServerStatHandler> getStatisticsMap();
}
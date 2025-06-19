package net.snowsign.snowdeath;

import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.snowsign.snowdeath.mixin.PlayerManagerAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class MixinUtil {
    public static @Nullable Integer getPlayerDeaths(MinecraftServer server, UUID player) {
        if (server == null) return null;

        Map<UUID, ServerStatHandler> statisticsMap =
            ((PlayerManagerAccessor) server.getPlayerManager()).getStatisticsMap();
        ServerStatHandler playerStatHandler = statisticsMap.get(player);

        if (playerStatHandler == null) return null;
        return playerStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));
    }
}

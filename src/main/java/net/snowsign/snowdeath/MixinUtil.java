package net.snowsign.snowdeath;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

public class MixinUtil {
    public static int getPlayerDeaths(ServerPlayerEntity player) {
        return player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));
    }
}

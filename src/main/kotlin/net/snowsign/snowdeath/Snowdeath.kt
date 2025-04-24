package net.snowsign.snowdeath

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Snowdeath : ModInitializer {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("snowdeath")
    }

    override fun onInitialize() {
        LOGGER.info("Loaded")
    }
}

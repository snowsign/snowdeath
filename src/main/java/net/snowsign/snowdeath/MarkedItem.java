package net.snowsign.snowdeath;

import java.util.UUID;

public interface MarkedItem {
    void snowdeath$mark(UUID deceased, int deaths);

    int snowdeath$getDeathCount();
}

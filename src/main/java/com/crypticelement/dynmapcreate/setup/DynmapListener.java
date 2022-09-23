package com.crypticelement.dynmapcreate.setup;

import com.crypticelement.dynmapcreate.DynmapCreate;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;

public class DynmapListener extends DynmapCommonAPIListener {
    @Override
    public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
        DynmapCreate.LOGGER.info("Dynmap API enabled");

        DynmapCreate.TRAINS.init(dynmapCommonAPI);
        DynmapCreate.RAILWAYS.init(dynmapCommonAPI);
        DynmapCreate.STATIONS.init(dynmapCommonAPI);
    }
}

package com.crypticelement.dynmapcreate;

import com.crypticelement.dynmapcreate.content.markers.TrackNetworkManager;
import com.crypticelement.dynmapcreate.content.markers.TrainMarkerManager;
import com.crypticelement.dynmapcreate.content.markers.TrainStationMarkerManager;
import com.crypticelement.dynmapcreate.setup.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

@Mod(DynmapCreate.MODID)
public class DynmapCreate {
    public static final String MODID = "dynmapcreate";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final TrainMarkerManager TRAINS = new TrainMarkerManager();
    public static final TrackNetworkManager RAILWAYS = new TrackNetworkManager();
    public static final TrainStationMarkerManager STATIONS = new TrainStationMarkerManager();

    public DynmapCreate() {
        // Make sure the mod being absent on the other network side does not
        // cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (remote, isServer) -> true
                )
        );

        Config.register();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}

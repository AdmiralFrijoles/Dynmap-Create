package com.crypticelement.dynmapcreate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

public class DynmapHelpers {
    public static String getWorldName(ResourceKey<Level> w) {
        String id = w.location().getNamespace() + "_" + w.location().getPath();
        switch (id) {
            case "minecraft_overworld":
                var server = ServerLifecycleHooks.getCurrentServer();
                var level = server.getLevel(w);
                return level != null ? level.serverLevelData.getLevelName() : null;
            case "minecraft_the_end":
                return "DIM1";

            case "minecraft_the_nether":
                return "DIM-1";

            default:
                return id;
        }
    }

    public static MarkerIcon createOrUpdateIcon(MarkerAPI markerAPI, String id, String label) {
        MarkerIcon icon = null;
        try {
            var iconRl = DynmapCreate.asResource("textures/markers/" + id + ".png");
            var resource = ServerLifecycleHooks.getCurrentServer().getResourceManager().getResource(iconRl);

            icon = markerAPI.getMarkerIcon(id);
            if (icon != null) {
                icon.setMarkerIconImage(resource.getInputStream());
                icon.setMarkerIconLabel(label);
            } else {
                icon = markerAPI.createMarkerIcon(id, label, resource.getInputStream());
            }
        }
        catch (Exception e) {
            DynmapCreate.LOGGER.error("Failed to create marker with id: " + id, e);
        }
        return icon;
    }
}

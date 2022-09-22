package com.crypticelement.dynmapcreate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

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
}

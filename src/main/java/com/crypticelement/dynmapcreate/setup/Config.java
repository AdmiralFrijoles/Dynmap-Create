package com.crypticelement.dynmapcreate.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static final String DEFAULT_TRAIN_MARKERSET_LABEL = "Trains";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue trainMarkersEnabled;
    public static final ForgeConfigSpec.ConfigValue<String> trainMarkerSetLabel;
    public static final ForgeConfigSpec.ConfigValue<String> trainNamePattern;
    public static final ForgeConfigSpec.BooleanValue trainMarkerShowLabel;

    static {
        BUILDER.push("Train Markers");
        trainMarkersEnabled = BUILDER.comment("Should the map track trains.").define("enabled", true);
        trainMarkerSetLabel = BUILDER.comment("Name for marker set for trains.").define("markerSetLabel", DEFAULT_TRAIN_MARKERSET_LABEL);
        trainNamePattern = BUILDER.comment("Regex pattern to filter trains that should have markers. Leave empty to show all trains.").define("trainNamePattern", "");
        trainMarkerShowLabel = BUILDER.comment("Should the train labels be shown by default.").define("showNames", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void register() {
        registerCommon();
    }

    private static void registerCommon() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC);
    }
}

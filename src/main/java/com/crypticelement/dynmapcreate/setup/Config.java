package com.crypticelement.dynmapcreate.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static final String DEFAULT_TRAIN_MARKERSET_LABEL = "Trains";
    public static final String DEFAULT_RAILWAY_MARKERSET_LABEL = "Railways";
    public static final String DEFAULT_STATION_MARKERSET_LABEL = "Train Stations";

    public static final String STATION_ICON_SMALL = "station_flag_small";
    public static final String STATION_ICON_LARGE = "station_flag_large";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue trainMarkersEnabled;
    public static final ForgeConfigSpec.ConfigValue<String> trainMarkerSetLabel;
    public static final ForgeConfigSpec.ConfigValue<String> trainNamePattern;
    public static final ForgeConfigSpec.BooleanValue trainMarkerShowLabel;
    public static final ForgeConfigSpec.BooleanValue trainMarkersHidden;
    public static final ForgeConfigSpec.IntValue trainMarkerLayer;

    public static final ForgeConfigSpec.BooleanValue railwayMarkersEnabled;
    public static final ForgeConfigSpec.ConfigValue<String> railwayMarkerSetLabel;
    public static final ForgeConfigSpec.ConfigValue<String> railwayMarkerColor;
    public static final ForgeConfigSpec.DoubleValue railwayMarkerOpacity;
    public static final ForgeConfigSpec.IntValue railwayMarkerLineWeight;
    public static final ForgeConfigSpec.BooleanValue railwayMarkersHidden;
    public static final ForgeConfigSpec.IntValue railwayMarkerLayer;

    public static final ForgeConfigSpec.BooleanValue stationMarkersEnabled;
    public static final ForgeConfigSpec.ConfigValue<String> stationMarkerSetLabel;
    public static final ForgeConfigSpec.BooleanValue stationMarkerShowLabel;
    public static final ForgeConfigSpec.BooleanValue stationMarkersHidden;
    public static final ForgeConfigSpec.IntValue stationMarkerLayer;
    public static final ForgeConfigSpec.ConfigValue<String> stationMarkerIcon;


    static {
        BUILDER.push("Train Markers");
        trainMarkersEnabled = BUILDER.comment("Should the map track trains?").define("enabled", true);
        trainMarkerSetLabel = BUILDER.comment("Name for marker set for trains.").define("markerSetLabel", DEFAULT_TRAIN_MARKERSET_LABEL);
        trainNamePattern = BUILDER.comment("Regex pattern to filter trains that should have markers. Leave empty to show all trains.").define("trainNamePattern", "");
        trainMarkerShowLabel = BUILDER.comment("Should the train labels be shown by default?").define("showNames", true);
        trainMarkersHidden = BUILDER.comment("Should train markers be hidden by default?").define("hideByDefault", false);
        trainMarkerLayer = BUILDER.defineInRange("markerSetLayer", 100, 0, 9999);
        BUILDER.pop();

        BUILDER.push("Railway Markers");
        railwayMarkersEnabled = BUILDER.comment("Should the map display rail lines?").define("enabled", true);
        railwayMarkerSetLabel = BUILDER.comment("Name for marker set for rail lines.").define("markerSetLabel", DEFAULT_RAILWAY_MARKERSET_LABEL);
        railwayMarkerColor = BUILDER.comment("The color of the rail lines as a hex value").define("lineColor", "#00A2E8");
        railwayMarkerOpacity = BUILDER.comment("The opacity of the rail lines.").defineInRange("lineOpacity", 1.0, 0.0, 1.0);
        railwayMarkerLineWeight = BUILDER.comment("The line weight for the rail lines.").defineInRange("lineWeight", 3, 1, 100);
        railwayMarkersHidden = BUILDER.comment("Should rail lines be hidden by default?").define("hideByDefault", false);
        railwayMarkerLayer = BUILDER.defineInRange("markerSetLayer", 90, 0, 9999);
        BUILDER.pop();

        BUILDER.push("Train Station Markers");
        stationMarkersEnabled = BUILDER.comment("Should the map track stations?").define("enabled", true);
        stationMarkerSetLabel = BUILDER.comment("Name for marker set for stations.").define("markerSetLabel", DEFAULT_STATION_MARKERSET_LABEL);
        stationMarkerShowLabel = BUILDER.comment("Should the station labels be shown by default?").define("showNames", false);
        stationMarkersHidden = BUILDER.comment("Should station markers be hidden by default?").define("hideByDefault", false);
        stationMarkerLayer = BUILDER.defineInRange("markerSetLayer", 95, 0, 9999);
        stationMarkerIcon = BUILDER.comment("The icon to use for station markers.").define("markerIcon", STATION_ICON_LARGE);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void register() {
        registerCommon();
    }

    private static void registerCommon() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    public static int parseColorFromString(String val) {
        try {
            if (val.charAt(0) == '#') {
                val = val.substring(1);
                return Integer.parseUnsignedInt(val, 16);
            }

            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0xFF0000;
        }
    }
}

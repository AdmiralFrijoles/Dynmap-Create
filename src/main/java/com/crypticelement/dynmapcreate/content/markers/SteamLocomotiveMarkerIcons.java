package com.crypticelement.dynmapcreate.content.markers;

import com.crypticelement.dynmapcreate.DynmapHelpers;
import net.minecraft.world.item.DyeColor;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

import java.util.HashMap;
import java.util.Map;

public class SteamLocomotiveMarkerIcons {
    public static final String STEAM_LOCOMOTIVE_BLACK = "steam_locomotive_black";
    public static final String STEAM_LOCOMOTIVE_BLUE = "steam_locomotive_blue";
    public static final String STEAM_LOCOMOTIVE_BROWN = "steam_locomotive_brown";
    public static final String STEAM_LOCOMOTIVE_CYAN = "steam_locomotive_cyan";
    public static final String STEAM_LOCOMOTIVE_GRAY = "steam_locomotive_gray";
    public static final String STEAM_LOCOMOTIVE_GREEN = "steam_locomotive_green";
    public static final String STEAM_LOCOMOTIVE_LIGHT_BLUE = "steam_locomotive_light_blue";
    public static final String STEAM_LOCOMOTIVE_LIGHT_GRAY = "steam_locomotive_light_gray";
    public static final String STEAM_LOCOMOTIVE_LIME = "steam_locomotive_lime";
    public static final String STEAM_LOCOMOTIVE_MAGENTA = "steam_locomotive_magenta";
    public static final String STEAM_LOCOMOTIVE_ORANGE = "steam_locomotive_orange";
    public static final String STEAM_LOCOMOTIVE_PINK = "steam_locomotive_pink";
    public static final String STEAM_LOCOMOTIVE_PURPLE = "steam_locomotive_purple";
    public static final String STEAM_LOCOMOTIVE_RED = "steam_locomotive_red";
    public static final String STEAM_LOCOMOTIVE_WHITE = "steam_locomotive_white";
    public static final String STEAM_LOCOMOTIVE_YELLOW = "steam_locomotive_yellow";

    private static final Map<DyeColor, String> STEAM_LOCOMOTIVE_MARKERS = new HashMap<>();

    private static MarkerAPI markerAPI;

    public static void register(MarkerAPI markerAPI) {
        SteamLocomotiveMarkerIcons.markerAPI = markerAPI;
        STEAM_LOCOMOTIVE_MARKERS.clear();
        createMarker(markerAPI, STEAM_LOCOMOTIVE_BLACK, DyeColor.BLACK, "Black Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_BLUE, DyeColor.BLUE, "Blue Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_BROWN, DyeColor.BROWN, "Brown Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_CYAN, DyeColor.CYAN, "Cyan Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_GRAY, DyeColor.GRAY, "Gray Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_GREEN, DyeColor.GREEN, "Green Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_LIGHT_BLUE, DyeColor.LIGHT_BLUE, "Light Blue Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_LIGHT_GRAY, DyeColor.LIGHT_GRAY, "Light Gray Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_LIME, DyeColor.LIME, "Lime Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_MAGENTA, DyeColor.MAGENTA, "Magenta Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_ORANGE, DyeColor.ORANGE, "Orange Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_PINK, DyeColor.PINK, "Pink Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_PURPLE, DyeColor.PURPLE, "Purple Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_RED, DyeColor.RED, "Red Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_WHITE, DyeColor.WHITE, "White Steam Locomotive");
        createMarker(markerAPI, STEAM_LOCOMOTIVE_YELLOW, DyeColor.YELLOW, "Yellow Steam Locomotive");
    }

    public static MarkerIcon getForColor(DyeColor color) {
        return markerAPI.getMarkerIcon(STEAM_LOCOMOTIVE_MARKERS.get(color));
    }

    private static void createMarker(MarkerAPI markerAPI, String id, DyeColor dyeColor, String label) {
        STEAM_LOCOMOTIVE_MARKERS.put(dyeColor, id);
        DynmapHelpers.createOrUpdateIcon(markerAPI, id, label);
    }
}

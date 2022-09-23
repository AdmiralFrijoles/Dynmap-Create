package com.crypticelement.dynmapcreate.content;

import com.crypticelement.dynmapcreate.DynmapHelpers;
import com.crypticelement.dynmapcreate.setup.Config;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.*;

public class TrainStationMarkerManager {
    private static final String MARKERSET = "create.stations";

    private MarkerAPI markerAPI = null;
    private MarkerSet stationMarkerSet = null;

    private Set<UUID> stationMarkers;
    private Set<UUID> markersToRemove;
    private Map<UUID, GlobalStation> allStations;

    public void init(DynmapCommonAPI dynmapCommonAPI) {
        if (!Config.stationMarkersEnabled.get())
            return;

        allStations = new HashMap<>();
        stationMarkers = new HashSet<>();
        markersToRemove = new HashSet<>();

        markerAPI = dynmapCommonAPI.getMarkerAPI();

        setupMarkerSet();
    }

    private void setupMarkerSet() {
        stationMarkerSet = markerAPI.getMarkerSet(MARKERSET);
        if (stationMarkerSet != null)
            stationMarkerSet.deleteMarkerSet();

        String markerSetLabel = Config.stationMarkerSetLabel.get();
        if (markerSetLabel == null || markerSetLabel.isBlank())
            markerSetLabel = "DEFAULT LABEL";

        stationMarkerSet = markerAPI.createMarkerSet(MARKERSET, markerSetLabel, null, false);
        stationMarkerSet.setLabelShow(Config.stationMarkerShowLabel.get());
        stationMarkerSet.setHideByDefault(Config.stationMarkersHidden.get());
    }

    public void tick() {
        if (markerAPI == null) return;
        updateStationMarkers();
    }

    private void updateStationMarkers() {
        allStations.clear();
        var graphs = Create.RAILWAYS.trackNetworks.values();
        for (var graph : graphs) {
            var stations = graph.getPoints(EdgePointType.STATION);
            for (var station : stations) {
                allStations.put(station.id, station);

                if (stationMarkers.contains(station.id)) {
                    updateStationMarker(station);
                } else {
                    addStationMarker(station);
                }
            }
        }

        markersToRemove.addAll(stationMarkers);
        for (var stationId : markersToRemove) {
            if (!allStations.containsKey(stationId))
                removeStationMarker(stationId);
        }
        markersToRemove.clear();
    }

    private void addStationMarker(GlobalStation station) {
        var markerId = station.id.toString();
        var stationName = station.name;
        var worldName = getStationWorldName(station);
        var position = station.tilePos;

        stationMarkerSet.createMarker(markerId, stationName, true, worldName,
                position.getX(), position.getY(), position.getZ(), null, false);

        stationMarkers.add(station.id);
    }

    private void updateStationMarker(GlobalStation station) {
        var marker = stationMarkerSet.findMarker(station.id.toString());
        if (marker == null) return;

        var stationName = station.name;

        marker.setLabel(stationName);
    }

    private void removeStationMarker(UUID stationId) {
        var marker = stationMarkerSet.findMarker(stationId.toString());
        if (marker != null) {
            marker.deleteMarker();
        }
        stationMarkers.remove(stationId);
    }

    private String getStationWorldName(GlobalStation station) {
         var trackNodeLocation = station.edgeLocation.getFirst();
         if (trackNodeLocation == null) trackNodeLocation = station.edgeLocation.getSecond();
         var dimension = trackNodeLocation.getDimension();
         if (dimension == null) return null;
         return DynmapHelpers.getWorldName(dimension);
    }
}

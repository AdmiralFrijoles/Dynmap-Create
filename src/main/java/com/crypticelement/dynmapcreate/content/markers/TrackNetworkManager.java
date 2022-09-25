package com.crypticelement.dynmapcreate.content.markers;

import com.crypticelement.dynmapcreate.DynmapHelpers;
import com.crypticelement.dynmapcreate.setup.Config;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.TrackEdge;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import net.minecraft.world.phys.Vec3;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.*;

public class TrackNetworkManager {
    private static final String MARKERSET = "create.railways";

    private MarkerAPI markerAPI = null;
    private MarkerSet railwayMarkerSet = null;
    private Set<String> edgeMarkers;
    private Set<String> validEdges;
    private Set<String> markersToRemove;

    public void init(DynmapCommonAPI dynmapCommonAPI) {
        if (!Config.railwayMarkersEnabled.get())
            return;

        markerAPI = dynmapCommonAPI.getMarkerAPI();
        setupMarkerSet();
    }

    private void setupMarkerSet() {
        edgeMarkers = new HashSet<>();
        validEdges = new HashSet<>();
        markersToRemove = new HashSet<>();

        railwayMarkerSet = markerAPI.getMarkerSet(MARKERSET);
        if (railwayMarkerSet != null)
            railwayMarkerSet.deleteMarkerSet();

        String marketSetLabel = Config.railwayMarkerSetLabel.get();
        if (marketSetLabel == null || marketSetLabel.isBlank())
            marketSetLabel = Config.DEFAULT_RAILWAY_MARKERSET_LABEL;

        railwayMarkerSet = markerAPI.createMarkerSet(MARKERSET, marketSetLabel, null, false);
        railwayMarkerSet.setHideByDefault(Config.railwayMarkersHidden.get());
        railwayMarkerSet.setLayerPriority(Config.railwayMarkerLayer.get());
    }

    public void tick() {
        if (markerAPI == null) return;

        var allNetworks = Create.RAILWAYS.trackNetworks;

        for (var graphEntry : allNetworks.entrySet()) {
            TrackGraph graph = graphEntry.getValue();
            for (var nodeLocation : graph.getNodes()) {
                if (nodeLocation == null) continue;
                var node = graph.locateNode(nodeLocation);
                if (node == null) continue;

                var map = graph.getConnectionsFrom(node);
                if (map == null || map.isEmpty()) continue;

                for (var entry : map.entrySet()) {
                    var edge = entry.getValue();
                    var id = getEdgeId(edge);

                    if (edge.isInterDimensional())
                        continue;

                    validEdges.add(id);
                    addOrUpdateEdgeMarker(id, edge);
                }
            }
        }

        // Remove edges that are no longer valid
        markersToRemove.addAll(edgeMarkers);
        markersToRemove.removeAll(validEdges);
        for (var edgeId : markersToRemove) {
            removeEdgeMarker(edgeId);
        }
        markersToRemove.clear();
        validEdges.clear();
    }

    private String getEdgeId(TrackEdge edge) {
        int hash = 31;
        hash = 31 * hash + edge.node1.getLocation().hashCode();
        hash = 31 * hash + edge.node2.getLocation().hashCode();
        return String.valueOf(hash);
    }

    private void removeEdgeMarker(String id) {
        edgeMarkers.remove(id);
        var marker = railwayMarkerSet.findPolyLineMarker(id);
        if (marker == null) return;
        marker.deleteMarker();
    }

    private void addOrUpdateEdgeMarker(String id, TrackEdge edge) {
        var worldName = DynmapHelpers.getWorldName(edge.node1.getLocation().dimension);
        var marker = railwayMarkerSet.findPolyLineMarker(id);
        if (marker == null) {
            var points = new ArrayList<Vec3>();
            if (!edge.isTurn()) {
                points.add(edge.getPosition(0));
                points.add(edge.getPosition(1));
            } else {
                var turn = edge.getTurn();
                for (int i = 0; i <= turn.getSegmentCount(); i++) {
                    var current = edge.getPosition(i * 1f / turn.getSegmentCount());
                    points.add(current);
                }
            }

            marker = railwayMarkerSet.createPolyLineMarker(
                    id, "", false, worldName,
                    points.stream().mapToDouble(Vec3::x).toArray(),
                    points.stream().mapToDouble(Vec3::y).toArray(),
                    points.stream().mapToDouble(Vec3::z).toArray(),
                    true
            );
            edgeMarkers.add(id);
        }

        var lineWeight = Config.railwayMarkerLineWeight.get();
        var color = Config.parseColorFromString(Config.railwayMarkerColor.get());
        double opacity = Config.railwayMarkerOpacity.get();
        marker.setLineStyle(lineWeight, opacity, color);
    }
}

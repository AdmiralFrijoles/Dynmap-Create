package com.crypticelement.dynmapcreate.content;

import com.crypticelement.dynmapcreate.DynmapCreate;
import com.crypticelement.dynmapcreate.DynmapHelpers;
import com.crypticelement.dynmapcreate.setup.Config;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.Train;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TrainMarkerManager {
    private static final String MARKERSET = "create.trains";

    private MarkerAPI markerAPI = null;
    private MarkerSet trainMarkerSet = null;
    private Set<UUID> trainMarkers;
    private Map<UUID, String> trainNames;
    private Set<UUID> invalidTrains;
    private Pattern trainNamePattern = null;
    private Set<UUID> markersToRemove;

    public void init(DynmapCommonAPI dynmapCommonAPI) {
        if (!Config.trainMarkersEnabled.get())
            return;

        trainMarkers = new HashSet<>();
        trainNames = new HashMap<>();
        invalidTrains = new HashSet<>();
        markersToRemove = new HashSet<>();

        markerAPI = dynmapCommonAPI.getMarkerAPI();

        SteamLocomotiveMarkerIcons.register(markerAPI);
        setupMarkerSet();
        setupTrainNamePattern();
    }

    private void setupTrainNamePattern() {
        var trainNamePatternRegex = Config.trainNamePattern.get();
        if (trainNamePatternRegex != null && !trainNamePatternRegex.isEmpty()) {
            try {
            trainNamePattern = Pattern.compile(trainNamePatternRegex);
            }
            catch (PatternSyntaxException e) {
                DynmapCreate.LOGGER.warn("Invalid regex pattern supplied for trainNamePattern");
            }
        }
    }

    private void setupMarkerSet() {
        trainMarkerSet = markerAPI.getMarkerSet(MARKERSET);
        if (trainMarkerSet != null)
            trainMarkerSet.deleteMarkerSet();

        String marketSetLabel = Config.trainMarkerSetLabel.get();
        if (marketSetLabel == null || marketSetLabel.isBlank())
            marketSetLabel = Config.DEFAULT_TRAIN_MARKERSET_LABEL;

        trainMarkerSet = markerAPI.createMarkerSet(MARKERSET, marketSetLabel, null, false);
        trainMarkerSet.setLabelShow(Config.trainMarkerShowLabel.get());
        trainMarkerSet.setDefaultMarkerIcon(SteamLocomotiveMarkerIcons.getForColor(DyeColor.WHITE));
    }

    public void tick() {
        if (markerAPI == null) return;
        updateTrains();
    }

    private void updateTrains() {
        var allTrains = Create.RAILWAYS.trains;

        // Remove cached info for trains that no longer exist
        invalidTrains.removeAll(allTrains.keySet());
        trainNames.keySet().removeAll(allTrains.keySet());

        // Check if invalid trains are now valid.
        invalidTrains.removeIf(trainId -> {
            var train = allTrains.get(trainId);
            return train == null || isTrainValid(train);
        });

        // Update existing train makers or add new ones
        for (var entry : allTrains.entrySet()) {
            var trainId = entry.getKey();
            if (invalidTrains.contains(trainId))
                continue;

            var train = entry.getValue();
            if (!isTrainValid(train)) {
                invalidTrains.add(trainId);
                continue;
            }

            if (trainMarkers.contains(trainId)) {
                updateTrainMarker(train);
            } else {
                addTrainMarker(train);
            }
        }

        // Remove train markers for trains that are no longer valid or do not exist.
        markersToRemove.addAll(trainMarkers);
        for (var trainId : markersToRemove) {
            if (!allTrains.containsKey(trainId) || invalidTrains.contains(trainId))
                removeTrainMarker(trainId);
        }
        markersToRemove.clear();
    }

    private void addTrainMarker(Train train) {
        var markerId = train.id.toString();
        var trainName = train.name.getString();
        var trainColor = getTrainColor(train);
        var icon = SteamLocomotiveMarkerIcons.getForColor(trainColor);
        var worldName = getTrainWorldName(train);
        var position = getTrainPosition(train);
        var description = getTrainMarkerDescription(train);

        var marker = trainMarkerSet.createMarker(markerId, trainName, true, worldName,
                position.x, position.y, position.z, icon, false);

        marker.setDescription(description);
        trainMarkers.add(train.id);
    }

    private void updateTrainMarker(Train train) {
        var marker = trainMarkerSet.findMarker(train.id.toString());
        if (marker == null) return;

        var trainName = train.name.getString();
        var worldName = getTrainWorldName(train);
        var position = getTrainPosition(train);
        var description = getTrainMarkerDescription(train);

        marker.setLabel(trainName);
        marker.setLocation(worldName, position.x, position.y, position.z);
        marker.setDescription(description);
    }

    private void removeTrainMarker(UUID trainId) {
        var marker = trainMarkerSet.findMarker(trainId.toString());
        if (marker != null) {
            marker.deleteMarker();
        }
        trainMarkers.remove(trainId);
    }

    private boolean isTrainValid(Train train) {
        if (train == null)
            return false;

        if (hasTrainNameChanged(train) && !isTrainNameValid(train))
            return false;

        if (train.carriages.get(0).presentInMultipleDimensions())
            return false;

        return true;
    }

    private boolean isTrainNameValid(Train train) {
        if (trainNamePattern == null) return true;

        var trainName = train.name.getString();
        var matcher = trainNamePattern.matcher(trainName);
        return matcher.matches();
    }

    private boolean hasTrainNameChanged(Train train) {
        if (!trainNames.containsKey(train.id))
            return true;

        var previousName = trainNames.get(train.id);
        var currentName = train.name.getString();

        trainNames.put(train.id, currentName);
        return !currentName.equals(previousName);
    }

    private DyeColor getTrainColor(Train train) {
        // Attempts to determine the train's color based on the color
        // of the closest seat block to the first control block found
        Vec3i controlBlockPos = null;

        // Find the first control block
        for(var carriage : train.carriages) {
            var entity = carriage.anyAvailableEntity();
            if (entity == null) continue;
            var contraption = entity.getContraption();
            for (var entry : contraption.getBlocks().entrySet()) {
                if (entry.getValue().state.is(AllBlocks.CONTROLS.get())) {
                    controlBlockPos = entry.getKey();
                }
            }
        }

        // All trains are required to have a control block, but just in-case we don't find one...
        if (controlBlockPos == null) {
            return DyeColor.WHITE;
        }

        // Now find the closet seat to the control block
        double minDist = Double.MAX_VALUE;
        DyeColor minDistSeatColor = DyeColor.WHITE;
        for(var carriage : train.carriages) {
            var entity = carriage.anyAvailableEntity();
            if (entity == null) continue;
            var contraption = entity.getContraption();
            for (var entry : contraption.getBlocks().entrySet()) {
                for (var seatBlock : AllBlocks.SEATS) {
                    if (entry.getValue().state.is(seatBlock.get())) {
                        var dist = entry.getKey().distSqr(controlBlockPos);
                        if (dist < minDist) {
                            minDist = dist;
                            minDistSeatColor = seatBlock.get().getColor();
                        }
                        break;
                    }
                }
            }
        }

        return minDistSeatColor;
    }

    private String getTrainWorldName(Train train) {
        var leadingBogey = train.carriages.get(0).leadingBogey();
        var dimension = leadingBogey.getDimension();
        if (dimension == null) return null;
        return DynmapHelpers.getWorldName(dimension);
    }

    private Vec3 getTrainPosition(Train train) {
        var leadingBogey = train.carriages.get(0).leadingBogey();
        return leadingBogey.leading().getPosition();
    }

    private String getTrainMarkerDescription(Train train) {
        var descriptionBuilder = new StringBuilder();

        if (train.derailed) {
            descriptionBuilder.append("This train has derailed!");
        }
        else {
            var currentStation = train.getCurrentStation();
            if (currentStation != null) {
                descriptionBuilder.append("At Station: ");
                descriptionBuilder.append(currentStation.name);
            } else {
                // train.speed is represented as blocks per tick. Multiplying by 20 gives us
                // blocks per second which is much easier for players to understand.
                descriptionBuilder.append(String.format("Speed: %.2f b/s", train.speed * 20));
            }

            var destination = train.navigation.destination;
            if (destination != null) {
                descriptionBuilder.append("<br/>");
                descriptionBuilder.append("Destination: ");
                descriptionBuilder.append(destination.name);
            }
        }

        return descriptionBuilder.toString();
    }
}

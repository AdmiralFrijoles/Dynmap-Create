package com.crypticelement.dynmapcreate.mixin;

import com.crypticelement.dynmapcreate.DynmapCreate;
import com.crypticelement.dynmapcreate.content.TrainWithCustomData;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.TrackNode;
import com.simibubi.create.content.logistics.trains.TrackNodeLocation;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraption;
import com.simibubi.create.content.logistics.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(value = com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity.class, remap = false)
public abstract class StationTileEntityMixin {
    @SuppressWarnings("rawtypes")
    @Inject(at = @At(value = "TAIL"),
            method= "assemble(Ljava/util/UUID;)V",
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onAssemble(UUID playerUUID, CallbackInfo ci, BlockPos trackPosition, BlockState trackState,
                           ITrackBlock track, BlockPos bogeyOffset, TrackNodeLocation location, Vec3 centre,
                           Collection ends, Vec3 targetOffset, List pointOffsets, int iPrevious, List points,
                           Vec3 directionVec, TrackGraph graph, TrackNode secondNode, List<CarriageContraption> contraptions,
                           List carriages, List spacing, boolean atLeastOneForwardControls, Train train) {

        determineMapColor(train, contraptions);
    }

    private void determineMapColor(Train train, List<CarriageContraption> contraptions) {
        // Attempts to determine the train's color based on the color
        // of the closest seat block to the first control block found
        Vec3i controlBlockPos = null;

        var trainWithCustomData = (TrainWithCustomData)train;

        // Find the first control block
        for (var contraption : contraptions) {
            for (var entry : contraption.getBlocks().entrySet()) {
                if (entry.getValue().state.is(AllBlocks.CONTROLS.get())) {
                    controlBlockPos = entry.getKey();
                }
            }
        }

        // All trains are required to have a control block, but just in-case we don't find one...
        if (controlBlockPos == null) {
            DynmapCreate.LOGGER.warn("Could not determine train color.");
            trainWithCustomData.setMapColor(DyeColor.WHITE);
            return;
        }

        // Now find the closet seat to the control block
        double minDist = Double.MAX_VALUE;
        DyeColor minDistSeatColor = DyeColor.WHITE;
        for (var contraption : contraptions) {
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

        trainWithCustomData.setMapColor(minDistSeatColor);
    }

}

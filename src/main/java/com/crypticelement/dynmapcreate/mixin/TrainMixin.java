package com.crypticelement.dynmapcreate.mixin;

import com.crypticelement.dynmapcreate.content.TrainWithCustomData;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.entity.Train;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = com.simibubi.create.content.logistics.trains.entity.Train.class, remap = false)
public abstract class TrainMixin implements TrainWithCustomData {
    private DyeColor mapColor = DyeColor.WHITE;
    @Override
    public DyeColor getMapColor() {
        return mapColor;
    }

    @Override
    public void setMapColor(DyeColor color) {
        mapColor = color;
    }

    @Inject(at = @At(value="TAIL"),
            method = "write(Lcom/simibubi/create/content/logistics/trains/DimensionPalette;)Lnet/minecraft/nbt/CompoundTag;"
    )
    public void onWrite(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        var customDataTag = new CompoundTag();
        customDataTag.putInt("MapColor", getMapColor().getId());

        cir.getReturnValue().put("dynmapcreate:data", customDataTag);
    }

    @Inject(at = @At(value="TAIL"),
            method = "read(Lnet/minecraft/nbt/CompoundTag;Ljava/util/Map;Lcom/simibubi/create/content/logistics/trains/DimensionPalette;)Lcom/simibubi/create/content/logistics/trains/entity/Train;"
    )
    private static void onRead(CompoundTag tag, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir) {
        var train = cir.getReturnValue();
        var trainWithCustomData = (TrainWithCustomData) train;

        var customDataTag = tag.getCompound("dynmapcreate:data");

        var mapColorId = customDataTag.getInt("MapColor");

        trainWithCustomData.setMapColor(DyeColor.byId(mapColorId));
    }
}


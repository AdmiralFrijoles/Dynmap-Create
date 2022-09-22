package com.crypticelement.dynmapcreate;
import com.crypticelement.dynmapcreate.setup.DynmapListener;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dynmap.DynmapCommonAPIListener;

@Mod.EventBusSubscriber(modid = DynmapCreate.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        DynmapCommonAPIListener.register(new DynmapListener());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        DynmapCreate.RAILWAYS.tick();
    }
}

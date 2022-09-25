package com.crypticelement.dynmapcreate;
import com.crypticelement.dynmapcreate.setup.DynmapListener;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dynmap.DynmapCommonAPIListener;

@Mod.EventBusSubscriber(modid = DynmapCreate.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    private static int tickTimer = 0;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        DynmapCommonAPIListener.register(new DynmapListener());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (--tickTimer <= 0) {
            DynmapCreate.TRAINS.tick();
            DynmapCreate.RAILWAYS.tick();
            DynmapCreate.STATIONS.tick();
            tickTimer = 20;
        }
    }
}

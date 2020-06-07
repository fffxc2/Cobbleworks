package fffxc2.cobbleworks;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class StoneEvent {
    private ArrayList<SpawnDefinition> spawnDefinitions;

    public StoneEvent(ArrayList<SpawnDefinition> spawnDefinitions) {
        this.spawnDefinitions = spawnDefinitions;
    }

    @SubscribeEvent
    public void stoneGen(BlockEvent.FluidPlaceBlockEvent event) {
        System.out.println("Event Block: "+event.getOriginalState().getBlock());
        for (SpawnDefinition definition : spawnDefinitions) {
            definition.handleEvent(event);
        }
    }
}

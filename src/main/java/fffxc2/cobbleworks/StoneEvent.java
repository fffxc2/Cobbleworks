package fffxc2.cobbleworks;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class StoneEvent {
    private ArrayList<SpawnDefinition> spawnDefinitions;

    public StoneEvent(ArrayList<SpawnDefinition> spawnDefinitions) {
        this.spawnDefinitions = spawnDefinitions;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void stoneGen(BlockEvent.FluidPlaceBlockEvent event) {
        for (SpawnDefinition definition : spawnDefinitions) {
            definition.handleEvent(event);
        }
    }
}

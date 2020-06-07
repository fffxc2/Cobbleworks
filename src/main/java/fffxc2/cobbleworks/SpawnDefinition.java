package fffxc2.cobbleworks;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class SpawnDefinition {
    public Block eventBlock;
    public Block reactionBlock;
    public ArrayList<SpawnDefinitionEntry> entries;

    public SpawnDefinition(@Nonnull Block eventBlock, @Nonnull Block reactionBlock, ArrayList<SpawnDefinitionEntry> entries) {
        this.eventBlock = eventBlock;
        this.reactionBlock = reactionBlock;
        this.entries = entries;
    }

    public void handleEvent(BlockEvent.FluidPlaceBlockEvent event) {
        System.out.println("Event for: "+event.getOriginalState().getBlock()+", checking reaction with: "+this.reactionBlock);
        if (event.getOriginalState().getBlock() != this.eventBlock) return;
        System.out.println("Matching reaction!");
        BlockPos eventPos = event.getLiquidPos(), waterSourcePos = null;
        IWorld world = event.getWorld();
        for (BlockPos pos : new BlockPos[]{eventPos.east(), eventPos.west(), eventPos.north(), eventPos.south()}) {
            if (world.getBlockState(pos).getBlock().equals(Blocks.WATER)) {
                waterSourcePos = pos;
                break;
            }
        }

        if (waterSourcePos != null) {
            BlockState targetBlock = null;

            for ( SpawnDefinitionEntry entry : entries) {
                System.out.println(entry);
                targetBlock = entry.apply(targetBlock, world.getBlockState(waterSourcePos).getFluidState().getLevel());
            }
            // If unknown, default don't change behavior
            if(targetBlock != null) {
                event.setNewState(targetBlock);
            }
        }
    }
//
    public static class SpawnDefinitionDeserializer implements JsonDeserializer<SpawnDefinition> {
        @Override
        public SpawnDefinition deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();

            String reactionBlockString;
//            if (root.has("catalyst")) {
//                reactionBlockString = getAsString(root, "catalyst");
//            } else {
                reactionBlockString = "minecraft:water";
//            }
            String eventBlockString = "minecraft:lava";

            JsonArray definition = root.getAsJsonArray("definition");
            if(definition.size() == 0) {
                throw new JsonParseException("Empty/Missing 'definition' !");
            }

            ArrayList<SpawnDefinitionEntry> entries = new ArrayList<>();
            SpawnDefinitionEntry.SpawnDefinitionEntryDeserializer deserializer = new SpawnDefinitionEntry.SpawnDefinitionEntryDeserializer();
            for (int i = 0; i < definition.size(); i++) {
                JsonElement element = definition.get(i);
                entries.add(deserializer.deserialize(element,type,context));
                if (!element.isJsonObject()) {
                    throw new JsonParseException("A part of 'definition' is not a compound object!");
                }
            }

            Block targetEventBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(eventBlockString));
            Block targetReactionBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(reactionBlockString));
            // Make sure the elements are in priority order
            Collections.sort(entries);
            return new SpawnDefinition(targetEventBlock, targetReactionBlock, entries);
        }
    }
}

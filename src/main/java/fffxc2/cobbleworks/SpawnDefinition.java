package fffxc2.cobbleworks;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
        BlockPos eventPos = event.getLiquidPos(), reactionPos = null;
        World world = event.getWorld();
        for (BlockPos pos : new BlockPos[]{eventPos.east(), eventPos.west(), eventPos.north(), eventPos.south(), eventPos.up(), eventPos.down()}) {
            if (world.getBlockState(pos).getBlock().equals(this.reactionBlock)) {
                reactionPos = pos;
                break;
            }
        }
        IBlockState targetBlock = null;
        if (reactionPos != null) {
            int reactionPosInt = world.getBlockState(reactionPos).getBlock().getMetaFromState(world.getBlockState(reactionPos));
            for ( SpawnDefinitionEntry entry : entries) {
                targetBlock = entry.apply(targetBlock, reactionPosInt);
            }
            // If unknown, default don't change behavior
            if(targetBlock != null) {
                event.setNewState(targetBlock);
            }
        }

    }

    public static class SpawnDefinitionDeserializer implements JsonDeserializer<SpawnDefinition> {
        @Override
        public SpawnDefinition deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();

            String reactionBlockString;
            if (JsonUtils.hasField(root, "catalyst")) {
                reactionBlockString = JsonUtils.getString(root, "catalyst");
            } else {
                reactionBlockString = "minecraft:water";
            }
            String eventBlockString = "minecraft:flowing_lava";


            JsonArray definition = JsonUtils.getJsonArray(root, "definition", new JsonArray());
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

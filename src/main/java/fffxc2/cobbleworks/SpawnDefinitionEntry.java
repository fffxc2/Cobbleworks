package fffxc2.cobbleworks;

import com.google.gson.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import java.lang.reflect.Type;

import static fffxc2.cobbleworks.Util.getBlockForResorceName;

public class SpawnDefinitionEntry implements Comparable<SpawnDefinitionEntry> {
    private final IBlockState replacementBlock;
    private final int distance;
    private final boolean allDistances;
    private final float chance;
    private final int priority;

    public SpawnDefinitionEntry(IBlockState replacementBlock, int distance, float chance, int priority) {
        this.replacementBlock = replacementBlock;
        this.distance = distance;
        this.allDistances = false;
        this.chance = chance;
        this.priority = priority;
    }

    public SpawnDefinitionEntry(IBlockState replacementBlock, float chance, int priority) {
        this.replacementBlock = replacementBlock;
        this.distance = 0;
        this.allDistances = true;
        this.chance = chance;
        this.priority = priority;
    }

    private boolean validDistance(int distance){
        return this.allDistances || this.distance == distance;
    }
    // Note, SpawnDefinition is responsible for making sure SpawnDefinitionEntrys apply in the right order
    public IBlockState apply(IBlockState currentBlock, int distance) {
        if (!validDistance(distance) || this.chance < Math.random()) {
            // If it's the wrong position or the chance roll fails make no changes
            return currentBlock;
        } else {
            // Otherwise update the block
            return replacementBlock;
        }
    }

    public int getPriority() {
        return priority;
    }

    public int compareTo(SpawnDefinitionEntry other){
        return this.priority - other.getPriority();
    }

    public static class SpawnDefinitionEntryDeserializer implements JsonDeserializer<SpawnDefinitionEntry> {
        @Override
        public SpawnDefinitionEntry deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            String replacementBlockString = JsonUtils.getString(root, "type");
            if(replacementBlockString.isEmpty()) {
                throw new JsonParseException("Invalid/Missing 'type' !");
            }
            IBlockState replacementBlock = getBlockForResorceName(replacementBlockString);

            float chance = 1.0f;
            if(JsonUtils.hasField(root, "chance")){
                chance = JsonUtils.getFloat(root, "chance");
            }

            int priority = 1;
            if(JsonUtils.hasField(root, "priority")) {
                priority = JsonUtils.getInt(root, "priority");
            }

            if(!JsonUtils.hasField(root, "distance")) {
                throw new JsonParseException("Invalid/missing 'distance' !");
            }

            if(JsonUtils.getString(root, "distance").equals("*")) {
                return new SpawnDefinitionEntry(replacementBlock, chance, priority);
            } else {
                int targetDistance = JsonUtils.getInt(root, "distance");
                return new SpawnDefinitionEntry(replacementBlock,targetDistance, chance, priority);
            }
        }
    }
}

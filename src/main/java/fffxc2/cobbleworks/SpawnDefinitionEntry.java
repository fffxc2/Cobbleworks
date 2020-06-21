package fffxc2.cobbleworks;

import com.google.gson.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.JsonUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static fffxc2.cobbleworks.Util.getBlockForResorceName;

public class SpawnDefinitionEntry implements Comparable<SpawnDefinitionEntry> {
    private final ArrayList<IBlockState> replacenmentBlockArray;
    private final int distance;
    private final boolean allDistances;
    private final float chance;
    private final int priority;

    public SpawnDefinitionEntry(ArrayList<IBlockState> replacenmentBlockArray, int distance, float chance, int priority) {
        this.replacenmentBlockArray = replacenmentBlockArray;
        this.distance = distance;
        this.allDistances = false;
        this.chance = chance;
        this.priority = priority;
    }

    public SpawnDefinitionEntry(ArrayList<IBlockState> replacenmentBlockArray, float chance, int priority) {
        this.replacenmentBlockArray = replacenmentBlockArray;
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
            return replacenmentBlockArray.get((int)(Math.random()*replacenmentBlockArray.size()));
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
            ArrayList<IBlockState> replacenmentBlockArray = new ArrayList<>();

            if (JsonUtils.hasField(root, "types")) {
                JsonArray blockStrings = JsonUtils.getJsonArray(root, "types");
                for (JsonElement blockString : blockStrings) {
                    replacenmentBlockArray.add(getBlockForResorceName(blockString.getAsString()));
                }
            } else {
                String replacementBlockString = JsonUtils.getString(root, "type");
                if (replacementBlockString.isEmpty()) {
                    throw new JsonParseException("Invalid/Missing 'type' !");
                }
                replacenmentBlockArray.add(getBlockForResorceName(replacementBlockString));
            }

            if (replacenmentBlockArray.size() == 0) {
                replacenmentBlockArray.add(Blocks.COBBLESTONE.getDefaultState());
            }

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

            String distance = JsonUtils.getString(root, "distance");
            if(distance.equals("*")) {
                return new SpawnDefinitionEntry(replacenmentBlockArray, chance, priority);
            } else {
                int targetDistance = Integer.parseInt(distance);
                return new SpawnDefinitionEntry(replacenmentBlockArray,targetDistance, chance, priority);
            }
        }
    }
}

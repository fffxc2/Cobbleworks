package fffxc2.cobbleworks;

import com.google.gson.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.lang.reflect.Type;
import static fffxc2.cobbleworks.Util.*;

public class SpawnDefinitionEntry implements Comparable<SpawnDefinitionEntry> {
    private final BlockState replacementBlock;
    private final int level;
    private final boolean allLevels;
    private final float chance;
    private final int priority;

    public SpawnDefinitionEntry(BlockState replacementBlock, int level, float chance, int priority) {
        this.replacementBlock = replacementBlock;
        this.level = level;
        this.allLevels = false;
        this.chance = chance;
        this.priority = priority;
    }

    public SpawnDefinitionEntry(BlockState replacementBlock, float chance, int priority) {
        this.replacementBlock = replacementBlock;
        this.level = 0;
        this.allLevels = true;
        this.chance = chance;
        this.priority = priority;
    }

    private boolean validFluidLevel(int distance){
        return this.allLevels || this.level == distance;
    }

    // Note, SpawnDefinition is responsible for making sure SpawnDefinitionEntrys apply in the right order
    public BlockState apply(BlockState currentBlock, int distance) {
        if (!validFluidLevel(distance) || this.chance < Math.random()) {
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

            String replacementBlockString = getAsString(root, "type");
            if(replacementBlockString.isEmpty()) {
                throw new JsonParseException("Invalid/Missing 'type' !");
            }
            BlockState replacementBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(replacementBlockString)).getDefaultState();

            float chance = 1.0f;
            if(root.has("chance")){
                chance = getAsFloat(root, "chance");
            }

            int priority = 1;
            if(root.has("priority")) {
                priority = getAsInt(root, "priority");
            }

            if(!root.has("level")) {
                throw new JsonParseException("Invalid/missing 'level' !");
            }

            if(getAsString(root, "level").equals("*")) {
                return new SpawnDefinitionEntry(replacementBlock, chance, priority);
            } else {
                int targetDistance = getAsInt(root, "level");
                return new SpawnDefinitionEntry(replacementBlock, targetDistance, chance, priority);
            }
        }
    }
}

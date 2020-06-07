package fffxc2.cobbleworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.ArrayList;

@Mod(Cobbleworks.MODID)
public class Cobbleworks
{
    public static final String MODID = "cobbleworks";
    public static final String NAME = "Cobbleworks";
    public static final String VERSION = "1.0-1.15.2";

    private static Logger logger;
    private File mainDir;

    public Cobbleworks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        String defaultJson = "{\n\t\"definition\": [\n" +
                    "\t\t{\n\t\t\t\"type\": \"minecraft:diorite\",\n\t\t\t\"level\": \"*\",\n\t\t\t\"chance\": 0.05\n\t\t},\n" +
                    "\t\t{\n\t\t\t\"type\": \"minecraft:andesite\",\n\t\t\t\"level\": \"*\",\n\t\t\t\"chance\": 0.05\n\t\t},\n" +
                    "\t\t{\n\t\t\t\"type\": \"minecraft:granite\",\n\t\t\t\"level\": \"*\",\n\t\t\t\"chance\": 0.05\n\t\t}\n" +
                    "\t]\n}";
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(SpawnDefinition.class, new SpawnDefinition.SpawnDefinitionDeserializer())
                .create();
        ArrayList<SpawnDefinition> spawnDefintions = new ArrayList<>();
        spawnDefintions.add(gson.fromJson(defaultJson, SpawnDefinition.class));
        MinecraftForge.EVENT_BUS.register(new StoneEvent(spawnDefintions));
    }
}

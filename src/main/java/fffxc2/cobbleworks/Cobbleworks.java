package fffxc2.cobbleworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

@Mod(modid = Cobbleworks.MODID, name = Cobbleworks.NAME, version = Cobbleworks.VERSION)
public class Cobbleworks
{
    public static final String MODID = "cobbleworks";
    public static final String NAME = "Cobbleworks";
    public static final String VERSION = "1.0";

    private static Logger logger;
    private File mainDir;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        File configDir = event.getModConfigurationDirectory();
        mainDir = new File(configDir, Cobbleworks.MODID);
        boolean copyDefault = false;
        if(!mainDir.exists()) {
            mainDir.mkdirs();
            copyDefault = true;
        }

        if(copyDefault) {
            // This is bad, but I'm not bothering to get resources set up just for this
            String defaultJson = "{\n\t\"definition\": [\n" +
                    "\t\t{\n\t\t\t\"type\": \"stone[variant=diorite]\",\n\t\t\t\"distance\": \"*\",\n\t\t\t\"chance\": 0.05\n\t\t},\n" +
                    "\t\t{\n\t\t\t\"type\": \"stone[variant=andesite]\",\n\t\t\t\"distance\": \"*\",\n\t\t\t\"chance\": 0.05\n\t\t},\n" +
                    "\t\t{\n\t\t\t\"type\": \"stone[variant=granite]\",\n\t\t\t\"distance\": \"*\",\n\t\t\t\"chance\": 0.05\n\t\t}\n" +
                    "\t]\n}";
            File defaultJsonFile = new File(mainDir,"default.json");
            try {
                Files.write(defaultJsonFile.toPath(), defaultJson.getBytes());
            } catch (Exception e) {
                System.out.println("Unable to write defaults file");
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ArrayList<SpawnDefinition> spawnDefintions = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(SpawnDefinition.class, new SpawnDefinition.SpawnDefinitionDeserializer())
                .create();

        for(File f : mainDir.listFiles()) {
            if (!f.getName().endsWith(".json")) { continue; }
            try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
                spawnDefintions.add(JsonUtils.fromJson(gson, isr, SpawnDefinition.class));
            } catch (Exception exc) {
                System.out.println("Failed to properly load file: "+f.getName());
            }
        }
        MinecraftForge.EVENT_BUS.register(new StoneEvent(spawnDefintions));
    }
}

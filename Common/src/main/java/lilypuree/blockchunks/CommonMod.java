package lilypuree.blockchunks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lilypuree.blockchunks.core.ReferenceHolder;
import lilypuree.blockchunks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommonMod {
    public static Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(ReferenceHolder.class, new ReferenceHolder.Serializer());
        GSON = builder.create();
    }

    public static boolean load() {
        Path configPath = Services.PLATFORM.getConfigPath();
        if (Files.exists(configPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(configPath);
                ReferenceHolder.INSTANCE = GSON.fromJson(reader, ReferenceHolder.class);
                reader.close();
                return true;
            } catch (IOException | JsonParseException e) {
                Constants.LOG.log(Level.ERROR, e.getMessage());
            }
        }
        ReferenceHolder.INSTANCE = new ReferenceHolder(Collections.emptySet());
        return false;
    }

    public static void scan() {
        Set<ResourceLocation> originBlocks = new HashSet<>();
        int i = 0;
        for (Block block : Registry.BLOCK) {
            if (block instanceof SlabBlock) {
                originBlocks.add(Registry.BLOCK.getKey(block));
                i++;
            }
        }
        Constants.LOG.log(Level.INFO, "blockchunks found {} slab blocks", i);
        serialize(new ReferenceHolder(originBlocks));
    }

    private static void serialize(ReferenceHolder config) {
        Path configPath = Services.PLATFORM.getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            BufferedWriter writer = Files.newBufferedWriter(configPath);
            GSON.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            Constants.LOG.log(Level.ERROR, e.getMessage());
        }
    }
}

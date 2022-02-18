package lilypuree.blockmagic;

import com.google.gson.*;
import lilypuree.blockmagic.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Configuration {
    private static Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        GSON = builder.create();
    }

    public Configuration(String name) {
        this.configFile = name + "_configuration";
        this.generatedFile = name + "_generated";
    }

    private String configFile;
    private String generatedFile;
    private boolean disableScanning = false;
    private boolean isGeneratedLoaded = false;

    public boolean scanningDisabled() {
        return disableScanning;
    }

    public boolean isGeneratedLoaded() {
        return isGeneratedLoaded;
    }

    public RawData read() {
        boolean[] disableScanning = new boolean[]{false};
        Set<ResourceLocation> generated = new HashSet<>();
        Set<ResourceLocation> whiteList = new HashSet<>();
        Set<ResourceLocation> blackList = new HashSet<>();
        loadJsonObject(configFile, json -> {
            loadSet(json, "whitelist", whiteList);
            loadSet(json, "blacklist", blackList);
            disableScanning[0] = GsonHelper.getAsBoolean(json, "disable_scanning");
        });
        this.disableScanning = disableScanning[0];
        isGeneratedLoaded = loadJsonObject(generatedFile, json -> {
            loadSet(json, "origin", generated);
        });
        return new RawData(generated, whiteList, blackList);
    }

    public void write(Set<ResourceLocation> generated) {
        writeToPath(configFile, true, () -> {
            JsonObject map = new JsonObject();
            map.add("whitelist", new JsonArray());
            map.add("blacklist", new JsonArray());
            map.addProperty("disable_scanning", false);
            return map;
        });
        writeToPath(generatedFile, disableScanning, () -> {
            JsonArray array = new JsonArray();
            generated.forEach(rl -> array.add(rl.toString()));
            JsonObject map = new JsonObject();
            map.add("origin", array);
            return map;
        });
    }

    private void loadSet(JsonObject json, String key, Set<ResourceLocation> dest) throws JsonParseException {
        if (json.has(key)) {
            JsonArray array = GsonHelper.getAsJsonArray(json, key);
            array.forEach(element -> {
                String string = GsonHelper.convertToString(element, key + " value");
                ResourceLocation loc = ResourceLocation.tryParse(string);
                if (loc != null) {
                    dest.add(loc);
                }
            });
        }
    }

    public static void writeToPath(String name, boolean skipIfPresent, Supplier<JsonElement> toWrite) {
        Path path = getConfigPath(name);
        if (skipIfPresent && Files.exists(path)) {
            return;
        }

        try {
            Files.createDirectories(path.getParent());
            BufferedWriter writer = Files.newBufferedWriter(path);
            GSON.toJson(toWrite.get(), writer);
            writer.close();
        } catch (IOException e) {
            Constants.LOG.log(Level.ERROR, e.getMessage());
        }
    }

    public static boolean loadJsonObject(String name, Consumer<JsonObject> action) {
        Path path = getConfigPath(name);
        if (Files.exists(path)) {
            try {
                BufferedReader reader = Files.newBufferedReader(path);
                JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                if (json != null) {
                    action.accept(json);
                    return true;
                }
                reader.close();
            } catch (IOException | JsonParseException e) {
                Constants.LOG.log(Level.ERROR, e.getMessage());
            }
        }
        return false;
    }

    private static Path getConfigPath(String name) {
        return Services.PLATFORM.getConfigPath().resolve(name + ".json");
    }

    public record RawData(Set<ResourceLocation> generated, Set<ResourceLocation> whiteList, Set<ResourceLocation> blackList) {

    }
}

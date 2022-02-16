package lilypuree.blockchunks.core;

import com.google.gson.*;
import lilypuree.blockchunks.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Type;
import java.util.*;

public class ReferenceHolder {
    public static String SIXWAY_SLAB = "sixway_slab";
    public static ReferenceHolder INSTANCE;
    private Map<String, SixwaySlabReference> sixwaySlabReferences;

    public ReferenceHolder(Set<ResourceLocation> originBlocks) {
        sixwaySlabReferences = new HashMap<>();
        originBlocks.stream().map(SixwaySlabReference::new).forEach(reference -> {
            sixwaySlabReferences.put(reference.getBaseName(), reference);
        });
    }

    public boolean notLoaded() {
        return true;
    }

    public Optional<SixwaySlabReference> getSixwaySlab(String baseName) {
        return Optional.ofNullable(sixwaySlabReferences.get(baseName));
    }

    public Collection<SixwaySlabReference> sixwaySlabs() {
        return sixwaySlabReferences.values();
    }

    public Optional<SixwaySlabReference> getSixwaySlab(ResourceLocation registryName) {
        if (registryName.getNamespace().equals(Constants.MOD_ID)) {
            String baseName = registryName.getPath().replace("_" + SIXWAY_SLAB, "");
            return Optional.ofNullable(sixwaySlabReferences.get(baseName));
        }
        return Optional.empty();
    }

    public void checkValidity() {
        Iterator<SixwaySlabReference> it = sixwaySlabReferences.values().iterator();
        while (it.hasNext()) {
            SixwaySlabReference reference = it.next();
            if (!Registry.BLOCK.containsKey(reference.getOrigin())) {
                Constants.LOG.log(Level.ERROR, "{} is not a registered block!", reference.getOrigin());
                it.remove();
            }
        }
    }

    public static class Serializer implements JsonSerializer<ReferenceHolder>, JsonDeserializer<ReferenceHolder> {
        @Override
        public ReferenceHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<ResourceLocation> originBlocks = new HashSet<>();
            if (json.isJsonObject()) {
                JsonElement elements = json.getAsJsonObject().get("source_blocks");
                if (elements.isJsonArray()) {
                    elements.getAsJsonArray().forEach(entry -> {
                        originBlocks.add(new ResourceLocation(entry.getAsString()));
                    });
                }
            }
            return new ReferenceHolder(originBlocks) {
                @Override
                public boolean notLoaded() {
                    return false;
                }
            };
        }

        @Override
        public JsonElement serialize(ReferenceHolder src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray(src.sixwaySlabReferences.size());
            src.sixwaySlabs().forEach(reference -> array.add(reference.getOrigin().toString()));
            JsonObject map = new JsonObject();
            map.add("source_blocks", array);
            return map;
        }
    }
}

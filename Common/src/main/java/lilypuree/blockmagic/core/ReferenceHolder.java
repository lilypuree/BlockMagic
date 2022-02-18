package lilypuree.blockmagic.core;

import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.block.SixwaySlabBlock;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.util.*;

public class ReferenceHolder {
    public static String SIXWAY_SLAB = "sixway_slab";
    private Map<String, BlockReference> sixwaySlabReferences;
    private Set<ResourceLocation> origins;

    public ReferenceHolder(Set<ResourceLocation> generated, Set<ResourceLocation> whitelist, Set<ResourceLocation> blacklist) {
        sixwaySlabReferences = new LinkedHashMap<>();
        generated.removeAll(blacklist);
        for (ResourceLocation blacklisted : blacklist) {
            if (whitelist.remove(blacklisted)) {
                Constants.LOG.log(Level.ERROR, "Duplicate entry {} in blacklist/whitelist", blacklisted);
            }
        }
        generated.addAll(whitelist);
        origins = generated;
        for (ResourceLocation originID : origins) {
            String baseName = getBaseName(originID);
            sixwaySlabReferences.put(baseName, new BlockReference(originID, baseName, SIXWAY_SLAB, new SixwaySlabBlock()));
        }
    }

    public Collection<BlockReference> values() {
        return sixwaySlabReferences.values();
    }

    public boolean containsParent(ResourceLocation id) {
        return origins.contains(id);
    }

    public Optional<BlockReference> getFromBaseName(String baseName) {
        return Optional.ofNullable(sixwaySlabReferences.get(baseName));
    }

    public BlockReference getFromParentID(ResourceLocation id) {
        return getFromBaseName(getBaseName(id)).get();
    }

    public Optional<BlockReference> getFromID(ResourceLocation registryName) {
        String baseName = registryName.getPath().replace("_" + SIXWAY_SLAB, "");
        return getFromBaseName(baseName);
    }

    public String getBaseName(ResourceLocation origin) {
        return origin.getPath().replace("_slab", "");
    }
}

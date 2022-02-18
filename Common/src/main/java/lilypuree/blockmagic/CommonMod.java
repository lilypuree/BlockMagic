package lilypuree.blockmagic;

import lilypuree.blockmagic.core.ReferenceHolder;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CommonMod {
    public static List<ResourceLocation> SLAB_BLOCKS = new ArrayList<>();
    public static Configuration SIXWAY_SLAB_CONFIG = new Configuration("sixway_slab");
    public static ReferenceHolder SIXWAY_SLABS;

    private static Duration timeTaken = Duration.ZERO;

    public static void addTime(Duration duration) {
        timeTaken = timeTaken.plus(duration);
    }

    static {
        Configuration.RawData data = SIXWAY_SLAB_CONFIG.read();
        SIXWAY_SLABS = new ReferenceHolder(data.generated(), data.whiteList(), data.blackList());
    }

    public static void init() {
    }

    public static void writeScanResults() {
        Constants.LOG.info("Time taken to resolve blocks: " + timeTaken.toMillis() + " ms");
        if (!SIXWAY_SLAB_CONFIG.scanningDisabled()) {
            SIXWAY_SLAB_CONFIG.write(SLAB_BLOCKS);
            Constants.LOG.log(Level.INFO, "{} found {} slab blocks", Constants.MOD_NAME, SLAB_BLOCKS.size());
        }
    }
}

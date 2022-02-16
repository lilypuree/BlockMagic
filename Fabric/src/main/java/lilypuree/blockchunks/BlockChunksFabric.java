package lilypuree.blockchunks;

import lilypuree.blockchunks.core.RegistryHelper;
import lilypuree.blockchunks.core.Registration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class BlockChunksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        if (CommonMod.load()) {
            Registration.registerBlocks(new RegistryHelperFabric<>(Registry.BLOCK));
            Registration.registerItems(new RegistryHelperFabric<>(Registry.ITEM));
        }
    }

    public static class RegistryHelperFabric<T> implements RegistryHelper<T> {
        Registry<T> registry;

        public RegistryHelperFabric(Registry<T> registry) {
            this.registry = registry;
        }

        @Override
        public void register(T entry, ResourceLocation name) {
            Registry.register(registry, name, entry);
        }
    }
}

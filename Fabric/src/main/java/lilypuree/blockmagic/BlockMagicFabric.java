package lilypuree.blockmagic;

import lilypuree.blockmagic.core.RegistryHelper;
import lilypuree.blockmagic.core.Registration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class BlockMagicFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonMod.onModConstruction();
        Registration.registerBlocks(new RegistryHelperFabric<>(Registry.BLOCK));
        Registration.registerItems(new RegistryHelperFabric<>(Registry.ITEM));
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

package lilypuree.blockchunks;

import lilypuree.blockchunks.core.RegistryHelper;
import lilypuree.blockchunks.core.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(value = Constants.MOD_ID)
public class BlockChunksForge {
    public BlockChunksForge() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        if (CommonMod.load()) {
            modbus.addGenericListener(Item.class, (RegistryEvent.Register<Item> e) -> Registration.registerItems(new RegistryHelperForge<>(e.getRegistry())));
            modbus.addGenericListener(Block.class, (RegistryEvent.Register<Block> e) -> Registration.registerBlocks(new RegistryHelperForge<>(e.getRegistry())));
        } else {
            modbus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(CommonMod::scan));
        }
    }

    public static class RegistryHelperForge<T extends IForgeRegistryEntry<T>> implements RegistryHelper<T> {
        IForgeRegistry<T> registry;

        public RegistryHelperForge(IForgeRegistry<T> registry) {
            this.registry = registry;
        }

        @Override
        public void register(T entry, ResourceLocation name) {
            registry.register(entry.setRegistryName(name));
        }
    }
}

package lilypuree.blockmagic.core;

import lilypuree.blockmagic.CommonMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Registration {
    public static void registerBlocks(RegistryHelper<Block> helper) {
        CommonMod.SIXWAY_SLABS.values().forEach(reference -> {
            helper.register(reference.getBlock(), reference.getRegistryName());
        });
    }

    public static void registerItems(RegistryHelper<Item> helper) {
        CommonMod.SIXWAY_SLABS.values().forEach(reference -> {
            helper.register(reference.createBlockItem(), reference.getRegistryName());
        });
    }
}

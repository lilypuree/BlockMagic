package lilypuree.blockmagic.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Registration {
    public static void registerBlocks(RegistryHelper<Block> helper) {
        ReferenceHolder.INSTANCE.checkValidity();
        ReferenceHolder.INSTANCE.sixwaySlabs().forEach(reference -> {
            helper.register(reference.getBlock(), reference.getRegistryName());
        });
    }

    public static void registerItems(RegistryHelper<Item> helper) {
        ReferenceHolder.INSTANCE.sixwaySlabs().forEach(reference -> {
            helper.register(reference.createBlockItem(), reference.getRegistryName());
        });
    }
}

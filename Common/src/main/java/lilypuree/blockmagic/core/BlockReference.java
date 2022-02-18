package lilypuree.blockmagic.core;

import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.block.DeferredBehaviorBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockReference {

    private DeferredBehaviorBlock block;
    private String baseName;
    private ResourceLocation origin;
    private ResourceLocation registryName;

    public BlockReference(ResourceLocation origin, String baseName, String suffix, DeferredBehaviorBlock block) {
        this.baseName = baseName;
        this.origin = origin;
        this.registryName = new ResourceLocation(Constants.MOD_ID, baseName + "_" + suffix);
        this.block = block;
    }

    public String getBaseName() {
        return baseName;
    }

    public ResourceLocation getOrigin() {
        return origin;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Block getOriginBlock() {
        return block.getParentBlock();
    }

    public Block getBlock() {
        return block;
    }

    public Item createBlockItem() {
        Item.Properties properties = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
        return new BlockItem(block, properties);
    }

    public void resolve(Block parentBlock) {
        this.block.resolve(parentBlock);
    }
}

package lilypuree.blockmagic.core;

import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.block.SixwaySlabBlock;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Map;

public class SixwaySlabReference {
    private SixwaySlabBlock sixwaySlabBlock;
    private Block originBlock;
    private ResourceLocation origin;
    private ResourceLocation registryName;

    public SixwaySlabReference(ResourceLocation origin) {
        this.origin = origin;
        this.registryName = new ResourceLocation(Constants.MOD_ID, getBaseName() + "_" + ReferenceHolder.SIXWAY_SLAB);
    }

    public ResourceLocation getOrigin() {
        return origin;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Block getOriginBlock() {
        return originBlock;
    }

    public Block getBlock() {
        if (this.sixwaySlabBlock == null) {
            this.originBlock = Registry.BLOCK.get(origin);
            this.sixwaySlabBlock = new SixwaySlabBlock(BlockBehaviour.Properties.copy(originBlock));
        }
        return sixwaySlabBlock;
    }

    public Item createBlockItem() {
        Item.Properties properties = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
        return new BlockItem(sixwaySlabBlock, properties);
    }


    public String getBaseName() {
        return origin.getPath().replace("_slab", "");
    }

    public String getTranslated(Map<String, String> translations) {
        String sixwaySlab = translations.get(Constants.MOD_ID + ".description.sixway_slab");
        String originTranslated = translations.get(originBlock.getDescriptionId());
        if (isOriginSlab()) {
            String slab = translations.get(Constants.MOD_ID + ".description.slab");
            return originTranslated.replace(slab, sixwaySlab);
        } else {
            return originTranslated + " " + sixwaySlab;
        }
    }

    public boolean isOriginSlab() {
        return originBlock instanceof SlabBlock;
    }
}

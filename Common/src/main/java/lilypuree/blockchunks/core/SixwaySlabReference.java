package lilypuree.blockchunks.core;

import lilypuree.blockchunks.Constants;
import lilypuree.blockchunks.block.SixwaySlabBlock;
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


    public Block getOriginBlock() {
        return originBlock;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public ResourceLocation getOrigin() {
        return origin;
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
        String sixwaySlab = translations.get("blockchunks.description.sixway_slab");
        String originTranslated = translations.get(originBlock.getDescriptionId());
        if (isOriginSlab()) {
            String slab = translations.get("blockchunks.description.slab");
            return originTranslated.replace(slab, sixwaySlab);
        } else {
            return originTranslated + " " + sixwaySlab;
        }
    }

    public Recipe<?> getRecipe() {
        if (isOriginSlab()) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(1, Ingredient.of(originBlock));
            return new ShapelessRecipe(registryName, "", new ItemStack(sixwaySlabBlock), ingredients);
        } else {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(3, Ingredient.of(originBlock));
            return new ShapedRecipe(registryName, "", 3, 1, ingredients, new ItemStack(sixwaySlabBlock));
        }
    }

    public boolean isOriginSlab() {
        return originBlock instanceof SlabBlock;
    }
}

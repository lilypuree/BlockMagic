package lilypuree.blockmagic.client;

import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.core.SixwaySlabReference;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

public class SixwaySlabModelHelper {

    private static String template_model = "{   'parent':   '%s',   'textures': {   'side': '%s',   'top':  '%s',   'bottom':   '%s'    }   }".replace('\'', '"');

    private static String template_item = "{    'parent':   '%s'}".replace('\'', '"');

    public static BlockModel getReplacedModel(String slabModelType, ResourceLocation top, ResourceLocation bottom, ResourceLocation side) {
        ResourceLocation parent = new ResourceLocation(Constants.MOD_ID, "block/" + slabModelType);
        String model = String.format(template_model, parent, side, top, bottom);
        return BlockModel.fromString(model);
    }

    public static BlockModel getItemModel(SixwaySlabReference reference) {
        ResourceLocation parent = new ResourceLocation(Constants.MOD_ID, "block/" + reference.getBaseName() + "_slab_down");
        String item_model = String.format(template_item, parent);
        return BlockModel.fromString(item_model);
    }
}

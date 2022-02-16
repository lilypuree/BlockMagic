package lilypuree.blockmagic.client;

import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.block.ModBlockProperties;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.core.Direction.*;

public class SixwaySlabBlockDefinition {

    private ResourceLocation defaultModel;
    private String baseName;
    public Map<String, MultiVariant> variants = new HashMap<>();

    public SixwaySlabBlockDefinition(String baseName, ResourceLocation defaultModel) {
        this.baseName = baseName;
        this.defaultModel = defaultModel;

        add(DOWN, UP, "", 0);
        add(UP, DOWN, "", 0);
        add(DOWN, DOWN, "down", 0);
        add(UP, UP, "up", 0);
        for (Direction horizontal : Plane.HORIZONTAL) {
            int rotation = ((int) horizontal.toYRot() + 90) % 360;
            add(UP, horizontal, "up_east", rotation);
            add(DOWN, horizontal, "down_east", rotation);
            add(horizontal, horizontal, "east", rotation);
            add(horizontal, horizontal.getOpposite(), "east_full", rotation);
            add(horizontal, UP, "east_up", rotation);
            add(horizontal, DOWN, "east_down", rotation);
            add(horizontal, horizontal.getCounterClockWise(), "east_north", rotation);
            add(horizontal, horizontal.getClockWise(), "east_south", rotation);
        }
    }

    private void add(Direction facing, Direction secondary, String modelSuffix, int rotation) {
        String property = String.format("%s=%s,%s=%s", BlockStateProperties.FACING.getName(), facing, ModBlockProperties.SECONDARY_FACING.getName(), secondary);
        BlockModelRotation modelRotation = BlockModelRotation.by(0, rotation);
        ResourceLocation modelLocation = modelSuffix.isEmpty() ? defaultModel : new ResourceLocation(Constants.MOD_ID, "block/" + baseName + "_slab_" + modelSuffix);
        Variant variant = new Variant(modelLocation, modelRotation.getRotation(), false, 1);
        variants.put(property, new MultiVariant(List.of(variant)));
    }
}

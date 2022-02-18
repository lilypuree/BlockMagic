package lilypuree.blockmagic.block;

import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.mixin.BlockBehaviorAccessor;
import lilypuree.blockmagic.mixin.BlockStateBaseAccessor;
import lilypuree.blockmagic.mixin.PropertiesAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.Level;

public class DeferredBehaviorBlock extends Block {
    private Block parentBlock;
    public static BlockBehaviour.Properties DEFAULT = Properties.of(Material.AIR);

    public DeferredBehaviorBlock() {
        super(DEFAULT);
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public Block resolve(Block parentBlock) {
        this.parentBlock = parentBlock;
        Properties property = Properties.copy(parentBlock);
        BlockBehaviorAccessor parent = ((BlockBehaviorAccessor) parentBlock);
        BlockBehaviorAccessor accessor = ((BlockBehaviorAccessor) this);
        accessor.setMaterial(parent.getMaterial());
        accessor.setHasCollision(parent.getHasCollision());
        accessor.setExplosionResistance(parent.getExplosionResistance());
        accessor.setIsRandomlyTicking(parent.getIsRandomlyTicking());
        accessor.setSoundType(parent.getSoundType());
        accessor.setFriction(parent.getFriction());
        accessor.setSpeedFactor(parent.getSpeedFactor());
        accessor.setDynamicShape(parent.getDynamicShape());
        accessor.setProperties(property);

        PropertiesAccessor propertiesAccessor = (PropertiesAccessor) property;

        for (BlockState state : stateDefinition.getPossibleStates()) {
            BlockStateBaseAccessor stateAccessor = (BlockStateBaseAccessor) state;
            stateAccessor.setLightEmission(propertiesAccessor.getLightEmission().applyAsInt(state));
//            stateAccessor.setUseShapeForLightOcclusion();
            stateAccessor.setIsAir(propertiesAccessor.getIsAir());
            stateAccessor.setMaterial(parent.getMaterial());
            stateAccessor.setMaterialColor(propertiesAccessor.getMaterialColor().apply(state));
            stateAccessor.setDestroySpeed(parentBlock.defaultDestroyTime());
            stateAccessor.setRequiresCorrectToolForDrops(propertiesAccessor.getRequiresCorrectToolForDrops());
            stateAccessor.setCanOcclude(propertiesAccessor.getCanOcclude());
            stateAccessor.setIsRedstoneConductor(propertiesAccessor.getIsRedstoneConductor());
            stateAccessor.setIsSuffocating(propertiesAccessor.getIsSuffocating());
            stateAccessor.setIsViewBlocking(propertiesAccessor.getIsViewBlocking());
            stateAccessor.setHasPostProcess(propertiesAccessor.getHasPostProcess());
            stateAccessor.setEmissiveRendering(propertiesAccessor.getEmissiveRendering());
        }
        return parentBlock;
    }
}

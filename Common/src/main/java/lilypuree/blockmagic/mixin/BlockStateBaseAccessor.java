package lilypuree.blockmagic.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.BlockStateBase.class)
public interface BlockStateBaseAccessor {

    @Mutable
    @Final
    @Accessor
    void setLightEmission(int lightEmission);

    @Mutable
    @Final
    @Accessor
    void setUseShapeForLightOcclusion(boolean useShapeForLightOcclusion);

    @Mutable
    @Final
    @Accessor
    void setIsAir(boolean isAir);

    @Mutable
    @Final
    @Accessor
    void setMaterial(Material material);

    @Mutable
    @Final
    @Accessor
    void setMaterialColor(MaterialColor materialColor);

    @Mutable
    @Final
    @Accessor
    void setDestroySpeed(float destroySpeed);

    @Mutable
    @Final
    @Accessor
    void setRequiresCorrectToolForDrops(boolean requiresCorrectToolForDrops);

    @Mutable
    @Final
    @Accessor
    void setCanOcclude(boolean canOcclude);

    @Mutable
    @Final
    @Accessor
    void setIsRedstoneConductor(BlockBehaviour.StatePredicate isRedstoneConductor);

    @Mutable
    @Final
    @Accessor
    void setIsSuffocating(BlockBehaviour.StatePredicate isSuffocating);

    @Mutable
    @Final
    @Accessor
    void setIsViewBlocking(BlockBehaviour.StatePredicate isViewBlocking);

    @Mutable
    @Final
    @Accessor
    void setHasPostProcess(BlockBehaviour.StatePredicate hasPostProcess);

    @Mutable
    @Final
    @Accessor
    void setEmissiveRendering(BlockBehaviour.StatePredicate emissiveRendering);
}

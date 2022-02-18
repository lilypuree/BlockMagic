package lilypuree.blockmagic.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@Mixin(BlockBehaviour.Properties.class)
public interface PropertiesAccessor {

    @Accessor
    ToIntFunction<BlockState> getLightEmission();

    @Accessor
    boolean getIsAir();

    @Accessor
    Function<BlockState, MaterialColor> getMaterialColor();

    @Accessor
    boolean getRequiresCorrectToolForDrops();

    @Accessor
    boolean getCanOcclude();

    @Accessor
    BlockBehaviour.StatePredicate getIsRedstoneConductor();

    @Accessor
    BlockBehaviour.StatePredicate getIsSuffocating();

    @Accessor
    BlockBehaviour.StatePredicate getIsViewBlocking();

    @Accessor
    BlockBehaviour.StatePredicate getHasPostProcess();

    @Accessor
    BlockBehaviour.StatePredicate getEmissiveRendering();
}

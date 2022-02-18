package lilypuree.blockmagic.mixin;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviorAccessor {

    @Mutable
    @Final
    @Accessor
    void setMaterial(Material material);

    @Accessor
    Material getMaterial();

    @Mutable
    @Final
    @Accessor
    void setHasCollision(boolean hasCollision);

    @Accessor
    boolean getHasCollision();

    @Mutable
    @Final
    @Accessor
    void setExplosionResistance(float explosionResistance);

    @Accessor
    float getExplosionResistance();

    @Mutable
    @Final
    @Accessor
    void setIsRandomlyTicking(boolean isRandomlyTicking);

    @Accessor
    boolean getIsRandomlyTicking();

    @Mutable
    @Final
    @Accessor
    void setSoundType(SoundType soundType);

    @Accessor
    SoundType getSoundType();

    @Mutable
    @Final
    @Accessor
    void setFriction(float friction);

    @Accessor
    float getFriction();

    @Mutable
    @Final
    @Accessor
    void setSpeedFactor(float speedFactor);

    @Accessor
    float getSpeedFactor();

    @Mutable
    @Final
    @Accessor
    void setJumpFactor(float jumpFactor);

    @Accessor
    float getJumpFactor();

    @Mutable
    @Final
    @Accessor
    void setDynamicShape(boolean dynamicShape);

    @Accessor
    boolean getDynamicShape();

    @Mutable
    @Final
    @Accessor
    void setProperties(BlockBehaviour.Properties properties);
}



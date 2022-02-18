package lilypuree.blockmagic.mixin;

import com.google.common.base.Stopwatch;
import com.google.common.collect.BiMap;
import lilypuree.blockmagic.CommonMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeRegistry.class)
public class ForgeRegistryMixin<V extends IForgeRegistryEntry<V>> {

    private boolean handled;

    @Shadow
    @Final
    private Class<V> superType;

    @Shadow
    @Final
    private BiMap<ResourceLocation, V> names;

    @Inject(method = "freeze", at = @At("TAIL"), remap = false)
    private void onFreeze(CallbackInfo ci) {
        if (superType == Block.class && !handled) {
            Stopwatch watch = Stopwatch.createStarted();
            this.names.forEach((key, value) -> {
                if (value instanceof SlabBlock) {
                    CommonMod.SLAB_BLOCKS.add(key);
                }
                if (CommonMod.SIXWAY_SLABS.containsParent(key)) {
                    CommonMod.SIXWAY_SLABS.getFromParentID(key).resolve((Block) value);
                }
            });
            watch.stop();
            CommonMod.addTime(watch.elapsed());
            handled = true;
        }
    }
}

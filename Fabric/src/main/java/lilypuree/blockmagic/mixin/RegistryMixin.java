package lilypuree.blockmagic.mixin;

import com.google.common.base.Stopwatch;
import lilypuree.blockmagic.CommonMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public class RegistryMixin {

    @Inject(method = "register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"))
    private static <V, T extends V> void onRegister(Registry<V> registry, ResourceLocation rl, T item, CallbackInfoReturnable<T> cir) {
        if (registry == Registry.BLOCK) {
            Stopwatch watch = Stopwatch.createStarted();
            if (item instanceof SlabBlock) {
                CommonMod.SLAB_BLOCKS.add(rl);
            }
            if (CommonMod.SIXWAY_SLABS.containsParent(rl)) {
                CommonMod.SIXWAY_SLABS.getFromParentID(rl).resolve((Block) item);
            }
            watch.stop();
            CommonMod.addTime(watch.elapsed());
            watch.reset();
        }
    }
}

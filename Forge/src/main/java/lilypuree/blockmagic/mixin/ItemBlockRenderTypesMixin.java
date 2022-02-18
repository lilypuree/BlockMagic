package lilypuree.blockmagic.mixin;

import lilypuree.blockmagic.block.DeferredBehaviorBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {

    @Shadow
    public static boolean canRenderInLayer(BlockState par1, RenderType par2) {
        return false;
    }

    @Inject(method = "canRenderInLayer(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onGetChunkRenderType(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> cir) {
        Block block = state.getBlock();
        if (block instanceof DeferredBehaviorBlock deferred) {
            Block parent = deferred.getParentBlock();
            if (parent != null) cir.setReturnValue(canRenderInLayer(parent.defaultBlockState(), type));
        }
    }
}

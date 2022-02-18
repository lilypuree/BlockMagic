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
    public static RenderType getChunkRenderType(BlockState $$0) {
        return null;
    }

    @Inject(method = "getChunkRenderType", at = @At("HEAD"), cancellable = true)
    private static void onGetChunkRenderType(BlockState state, CallbackInfoReturnable<RenderType> cir) {
        Block block = state.getBlock();
        if (block instanceof DeferredBehaviorBlock deferred) {
            Block parent = deferred.getParentBlock();
            if (parent != null) cir.setReturnValue(getChunkRenderType(parent.defaultBlockState()));
        }
    }
}

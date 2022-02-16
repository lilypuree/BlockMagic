package lilypuree.blockchunks.mixin;

import lilypuree.blockchunks.CommonMod;
import lilypuree.blockchunks.core.ReferenceHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "run", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        if (ReferenceHolder.INSTANCE.notLoaded()) {
            CommonMod.scan();
        }
    }
}

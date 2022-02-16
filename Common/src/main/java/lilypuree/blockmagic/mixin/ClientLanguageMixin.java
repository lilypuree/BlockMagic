package lilypuree.blockmagic.mixin;

import lilypuree.blockmagic.core.ReferenceHolder;
import lilypuree.blockmagic.core.SixwaySlabReference;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(ClientLanguage.class)
public class ClientLanguageMixin {

    @Inject(method = "loadFrom", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onLoadFrom(ResourceManager manager, List<LanguageInfo> languageInfos, CallbackInfoReturnable<ClientLanguage> cir, Map<String, String> localStorage) {
        for (SixwaySlabReference reference : ReferenceHolder.INSTANCE.sixwaySlabs()) {
            if (!localStorage.containsKey(reference.getBlock().getDescriptionId()))
                localStorage.put(reference.getBlock().getDescriptionId(), reference.getTranslated(localStorage));
        }
    }
}

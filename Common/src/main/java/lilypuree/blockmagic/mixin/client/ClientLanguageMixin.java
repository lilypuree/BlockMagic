package lilypuree.blockmagic.mixin.client;

import lilypuree.blockmagic.CommonMod;
import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.core.BlockReference;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.SlabBlock;
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
        for (BlockReference reference : CommonMod.SIXWAY_SLABS.values()) {
            if (!localStorage.containsKey(reference.getBlock().getDescriptionId()))
                localStorage.put(reference.getBlock().getDescriptionId(), sixwaySlabTranslation(localStorage, reference));
        }
    }

    private static String sixwaySlabTranslation(Map<String, String> translations, BlockReference reference) {
        String sixwaySlab = translations.get(Constants.MOD_ID + ".description.sixway_slab");
        String originTranslated = translations.get(reference.getOriginBlock().getDescriptionId());
        if (reference.getOriginBlock() instanceof SlabBlock) {
            String slab = translations.get(Constants.MOD_ID + ".description.slab");
            return originTranslated.replace(slab, sixwaySlab);
        } else {
            return originTranslated + " " + sixwaySlab;
        }
    }
}

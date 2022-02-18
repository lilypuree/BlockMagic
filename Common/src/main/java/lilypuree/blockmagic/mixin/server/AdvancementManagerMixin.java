package lilypuree.blockmagic.mixin.server;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lilypuree.blockmagic.CommonMod;
import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.core.BlockReference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Mixin(ServerAdvancementManager.class)
public class AdvancementManagerMixin {

    @Shadow
    @Final
    private static Gson GSON;
    private static String advancementTemplate = """
            {  'parent':   'minecraft:recipes/root',
            'rewards':  {'recipes': ['%s']},
            'criteria': {'has_base_block':  {'trigger': 'minecraft:inventory_changed',  'conditions':   {'items':   [{'items': ['%s']}]}},
             'has_the_recipe':  {'trigger': 'minecraft:recipe_unlocked', 'conditions':{'recipe': '%s'}}},
            'requirements':[['has_base_block', 'has_the_recipe']]}""".replace('\'', '"');


    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "HEAD"))
    private void onApply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
        for (BlockReference reference : CommonMod.SIXWAY_SLABS.values()) {
            ResourceLocation registryName = reference.getRegistryName();
            String recipeName = registryName.toString();
            String itemName = Registry.ITEM.getKey(reference.getOriginBlock().asItem()).toString();

            String advancementString = advancementTemplate.formatted(recipeName, itemName, recipeName);
            JsonElement element = GsonHelper.fromJson(GSON, new StringReader(advancementString), JsonElement.class);
            jsonMap.put(new ResourceLocation(registryName.getNamespace(), "recipes/building_blocks/" + registryName.getPath()), element);
        }
    }
}

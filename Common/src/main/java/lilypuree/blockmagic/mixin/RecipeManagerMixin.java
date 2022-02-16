package lilypuree.blockmagic.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lilypuree.blockmagic.core.ReferenceHolder;
import lilypuree.blockmagic.core.SixwaySlabReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.StringReader;
import java.util.*;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    private static String shapeless_template = """
            {'type' : 'minecraft:crafting_shapeless',   'ingredients':[{'item':'%s'}],  'result':{'item':'%s'}}
            """.replace('\'', '"');
    private static String shaped_template = """
            {'type' : 'minecraft:crafting_shaped',   'pattern':['###'], 'key':{'#':{'item':'%s'}},  'result':{'item':'%s','count':6}}
            """.replace('\'', '"');

    @Shadow
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    @Shadow
    @Final
    private static Gson GSON;

    //injects all recipes into the map before other recipes are made
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "HEAD"))
    private void onApply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler, CallbackInfo ci) {
        for (SixwaySlabReference reference : ReferenceHolder.INSTANCE.sixwaySlabs()) {
            ResourceLocation recipeName = reference.getRegistryName();
            String ingredient = reference.getOrigin().toString();
            String result = reference.getRegistryName().toString();
            String recipeString = reference.isOriginSlab() ? shapeless_template.formatted(ingredient, result) : shaped_template.formatted(ingredient, result);
            JsonElement element = GsonHelper.fromJson(GSON, new StringReader(recipeString), JsonElement.class);
            map.put(recipeName, element);
        }
    }

    //fix the group names based on the recip for the original slab
    //runs after all other recipes are made
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onRecipeBuild(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> builderMap, ImmutableMap.Builder<ResourceLocation, Recipe<?>> byNameMap) {
        Map<Item, Recipe<?>> slabRecipes = new IdentityHashMap<>();
        for (SixwaySlabReference reference : ReferenceHolder.INSTANCE.sixwaySlabs()) {
            if (reference.isOriginSlab()) {
                Recipe<?> recipe = this.recipes.get(RecipeType.CRAFTING).get(reference.getRegistryName());
                slabRecipes.put(reference.getOriginBlock().asItem(), recipe);
            }
        }
        this.recipes.get(RecipeType.CRAFTING).values().stream()
                .filter(recipe -> slabRecipes.containsKey(recipe.getResultItem().getItem()))
                .forEach(recipe -> {
                    if (!recipe.getGroup().isEmpty()) {
                        Recipe<?> slabRecipe = slabRecipes.get(recipe.getResultItem().getItem());
                        ((ShapelessRecipeAccessor) slabRecipe).setGroup(recipe.getGroup() + "_sixway");
                    }
                });
    }
}

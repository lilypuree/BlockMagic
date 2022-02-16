package lilypuree.blockmagic.mixin;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lilypuree.blockmagic.core.ReferenceHolder;
import lilypuree.blockmagic.core.SixwaySlabReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {

    @Shadow
    @Final
    private String directory;

    @Shadow
    private static void visitDependenciesAndElement(Map<ResourceLocation, Tag.Builder> $$0, Multimap<ResourceLocation, ResourceLocation> $$1, Set<ResourceLocation> $$2, ResourceLocation $$3, BiConsumer<ResourceLocation, Tag.Builder> $$4) {
    }

    private static String SOURCE = "blockmagic_cursed_mixin";

    private static ResourceLocation[] blockTagsToCheck = new ResourceLocation[]{
            new ResourceLocation("mineable/axe"),
            new ResourceLocation("mineable/pickaxe"),
            new ResourceLocation("mineable/shovel"),
            new ResourceLocation("needs_diamond_tool"),
            new ResourceLocation("needs_iron_tool"),
            new ResourceLocation("needs_stone_tool"),
            new ResourceLocation("non_flammable_wood")
    };

    private static ResourceLocation[] itemTagsToCheck = new ResourceLocation[]{
            new ResourceLocation("non_flammable_wood")
    };

    @Inject(method = "build", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onBuild(Map<ResourceLocation, Tag.Builder> builders, CallbackInfoReturnable<TagCollection> cir, Map tagMap, Function tagGetter, Function valueGetter,
                         Multimap<ResourceLocation, ResourceLocation> dependencyNames) {
        if (this.directory.equals("tags/blocks")) {
            for (ResourceLocation tagName : blockTagsToCheck) {
                addAllSixwaySlabs(builders, dependencyNames, tagName);
            }
        }
        if (this.directory.equals("tags/items")) {
            for (ResourceLocation tagName : itemTagsToCheck) {
                addAllSixwaySlabs(builders, dependencyNames, tagName);
            }
        }
    }

    private void addAllSixwaySlabs(Map<ResourceLocation, Tag.Builder> builders, Multimap<ResourceLocation, ResourceLocation> dependencyNames, ResourceLocation tagName) {
        Tag.Builder builder = builders.get(tagName);
        for (SixwaySlabReference reference : ReferenceHolder.INSTANCE.sixwaySlabs()) {
            String originName = reference.getOrigin().toString();
            boolean[] contains = new boolean[]{false};
            visitDependenciesAndElement(builders, dependencyNames, Sets.newHashSet(), tagName, (name, childBuilder) -> {
                contains[0] = contains[0] || childBuilder.getEntries().map(Tag.BuilderEntry::getEntry).anyMatch(entry -> {
                    if (entry instanceof Tag.ElementEntry) {
                        return entry.toString().equals(originName);
                    }
                    return false;
                });
            });

            if (contains[0]) {
                builder.addElement(reference.getRegistryName(), SOURCE);
            }
        }
    }
}

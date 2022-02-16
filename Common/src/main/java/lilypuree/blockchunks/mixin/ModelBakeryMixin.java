package lilypuree.blockchunks.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import lilypuree.blockchunks.Constants;
import lilypuree.blockchunks.client.SixwaySlabBlockDefinition;
import lilypuree.blockchunks.client.SixwaySlabModelHelper;
import lilypuree.blockchunks.core.ReferenceHolder;
import lilypuree.blockchunks.core.SixwaySlabReference;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation $$0);

    @Shadow
    private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> $$0, String $$1) {
        throw new AssertionError();
    }

    @Shadow
    protected abstract void cacheAndQueueDependencies(ResourceLocation $$0, UnbakedModel $$1);

    @Shadow
    protected abstract void registerModelGroup(Iterable<BlockState> $$0);

    @Shadow
    @Final
    public static ModelResourceLocation MISSING_MODEL_LOCATION;

    @Shadow
    public abstract BakedModel bake(ResourceLocation $$0, ModelState $$1);

    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModelDefinition$Context;setDefinition(Lnet/minecraft/world/level/block/state/StateDefinition;)V"), cancellable = true)
    private void onLoadModel(ResourceLocation location, CallbackInfo ci) {
        if (location.getNamespace().equals(Constants.MOD_ID)) {
            ResourceLocation newLocation = new ResourceLocation(location.getNamespace(), location.getPath());
            ReferenceHolder.INSTANCE.getSixwaySlab(newLocation).ifPresent(reference -> {
                loadSixwaySlab(reference);
                ci.cancel();
            });
        }
    }

    private void loadSixwaySlab(SixwaySlabReference reference) {
        StateDefinition<Block, BlockState> stateDefinition = reference.getBlock().getStateDefinition();

        //create a map of all possible model resource locations from each blockstate to blockstates
        ImmutableList<BlockState> possibleStates = stateDefinition.getPossibleStates();
        Map<ModelResourceLocation, BlockState> modelLocationBlockStateMap = Maps.newHashMap();
        possibleStates.forEach((state) -> modelLocationBlockStateMap.put(BlockModelShaper.stateToModelLocation(reference.getRegistryName(), state), state));

        Map<BlockState, UnbakedModel> statesToUnbakedModels = new IdentityHashMap<>();
        ResourceLocation defaultModel = getDefaultModel(reference.getOriginBlock());

        SixwaySlabBlockDefinition definition = new SixwaySlabBlockDefinition(reference.getBaseName(), defaultModel);
        definition.variants.forEach((properties, variant) -> {
            possibleStates.stream().filter(predicate(stateDefinition, properties)).forEach(state -> {
                statesToUnbakedModels.put(state, variant);
            });
        });

        modelLocationBlockStateMap.forEach((modelLocationEntry, stateEntry) -> {
            UnbakedModel unbakedModel = statesToUnbakedModels.get(stateEntry);
            this.cacheAndQueueDependencies(modelLocationEntry, unbakedModel);
        });

        this.registerModelGroup(possibleStates);
    }

    @Inject(method = "loadBlockModel", at = @At("HEAD"), cancellable = true)
    private void onLoadBlockModel(ResourceLocation location, CallbackInfoReturnable<BlockModel> cir) {
        if (location.getNamespace().equals(Constants.MOD_ID)) {
            String[] parts = location.getPath().split("/");
            if (parts[0].equals("item")) {
                ReferenceHolder.INSTANCE.getSixwaySlab(new ResourceLocation(Constants.MOD_ID, parts[1])).map(SixwaySlabModelHelper::getItemModel)
                        .ifPresent(model -> {
                            model.name = location.toString();
                            cir.setReturnValue(model);
                        });
            } else if (parts[0].equals("block")) {
                int suffixIndex = parts[1].lastIndexOf("slab");
                if (suffixIndex <= 0) return;
                String slabType = parts[1].substring(suffixIndex);
                String baseName = parts[1].substring(0, suffixIndex - 1); //remove the underscore

                ReferenceHolder.INSTANCE.getSixwaySlab(baseName).ifPresent(reference -> {
                    BlockModel model = createBlockModel(slabType, reference);
                    if (model != null) {
                        model.name = location.toString();
                        cir.setReturnValue(model);
                    }
                });
            }
        }
    }


    private BlockModel createBlockModel(String slabType, SixwaySlabReference reference) {
        ResourceLocation parentModel = getDefaultModel(reference.getOriginBlock());
        UnbakedModel parent = this.getModel(parentModel);
        if (parent instanceof BlockModel blockParent) {
            Material top, bottom, side;
            if (blockParent.hasTexture("#all")) {
                top = bottom = side = blockParent.getMaterial("#all");
            } else if (blockParent.hasTexture("#end") && blockParent.hasTexture("#side")) {
                top = bottom = blockParent.getMaterial("#end");
                side = blockParent.getMaterial("#side");
            } else if (blockParent.hasTexture("#top") && blockParent.hasTexture("#side") && blockParent.hasTexture("#bottom")) {
                top = blockParent.getMaterial("#top");
                bottom = blockParent.getMaterial("#bottom");
                side = blockParent.getMaterial("#side");
            } else {
                Set<Pair<String, String>> missingTextures = Sets.newLinkedHashSet();
                Optional<Material> material = blockParent.getMaterials(this::getModel, missingTextures).stream().findFirst();
                if (material.isPresent()) {
                    top = bottom = side = material.get();
                } else {
                    return null;
                }
            }
            return SixwaySlabModelHelper.getReplacedModel(slabType, top.texture(), bottom.texture(), side.texture());
        }
        return null;
    }

    private ResourceLocation getDefaultModel(Block block) {
        BlockState defaultState = block.defaultBlockState();
        if (block instanceof SlabBlock) {
            defaultState = defaultState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
        }
        UnbakedModel model = this.getModel(BlockModelShaper.stateToModelLocation(defaultState));
        if (model instanceof MultiVariant multiVariant) {
            return multiVariant.getVariants().get(0).getModelLocation();
        } else if (model instanceof MultiPart multiPart) {
            return multiPart.getSelectors().get(0).getVariant().getVariants().get(0).getModelLocation();
        } else if (model instanceof BlockModel blockModel && !blockModel.name.isEmpty()) {
            return new ResourceLocation(blockModel.name);
        }
        return MISSING_MODEL_LOCATION;
    }
}

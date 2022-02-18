package lilypuree.blockmagic.mixin.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lilypuree.blockmagic.CommonMod;
import lilypuree.blockmagic.Constants;
import lilypuree.blockmagic.client.BlockDefinitionProvider;
import lilypuree.blockmagic.client.SixwaySlabBlockDefinition;
import lilypuree.blockmagic.client.SixwaySlabModelHelper;
import lilypuree.blockmagic.core.BlockReference;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
import java.util.function.Predicate;

import static net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION;

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

    private UnbakedModel parentModel = null;

    /**
     * Before the blockstate models are loaded, load the parent model first, and save the result to use later
     */
    @Inject(method = "loadTopLevel", at = @At("HEAD"))
    private void onLoadTopLevel(ModelResourceLocation location, CallbackInfo ci) {
        if (location.getNamespace().equals(Constants.MOD_ID)) {
            ResourceLocation newLocation = new ResourceLocation(location.getNamespace(), location.getPath());
            parentModel = CommonMod.SIXWAY_SLABS.getFromID(newLocation).map(reference -> {
                return getDefaultModel(reference.getOriginBlock());
            }).map(this::getModel).orElse(null);
        }
    }

    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModelDefinition$Context;setDefinition(Lnet/minecraft/world/level/block/state/StateDefinition;)V"), cancellable = true)
    private void onLoadModel(ResourceLocation location, CallbackInfo ci) {
        if (location.getNamespace().equals(Constants.MOD_ID)) {
            ResourceLocation newLocation = new ResourceLocation(location.getNamespace(), location.getPath());
            CommonMod.SIXWAY_SLABS.getFromID(newLocation).ifPresent(reference -> {
                loadSixwaySlab(reference, new SixwaySlabBlockDefinition(reference.getBaseName()));
                ci.cancel();
            });
        }
    }

    /**
     * cache and queue dependencies for all states of the given Block
     * uses the BlockDefinitionProvider to obtain MultiVariants for each state
     */
    private void loadSixwaySlab(BlockReference reference, BlockDefinitionProvider blockDefinitionProvider) {
        StateDefinition<Block, BlockState> stateDefinition = reference.getBlock().getStateDefinition();

        //create a map of all possible model resource locations from each blockstate to blockstates
        ImmutableList<BlockState> possibleStates = stateDefinition.getPossibleStates();
        Map<ModelResourceLocation, BlockState> modelLocationBlockStateMap = Maps.newHashMap();
        possibleStates.forEach((state) -> modelLocationBlockStateMap.put(BlockModelShaper.stateToModelLocation(reference.getRegistryName(), state), state));

        Map<BlockState, UnbakedModel> statesToUnbakedModels = new IdentityHashMap<>();

        blockDefinitionProvider.getVariants().forEach((properties, variant) -> {
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
                CommonMod.SIXWAY_SLABS.getFromID(new ResourceLocation(Constants.MOD_ID, parts[1])).map(SixwaySlabModelHelper::getItemModel)
                        .ifPresent(model -> {
                            model.name = location.toString();
                            cir.setReturnValue(model);
                        });
            } else if (parts[0].equals("block")) {
                int suffixIndex = parts[1].lastIndexOf("slab");
                if (suffixIndex <= 0) return;
                String slabType = parts[1].substring(suffixIndex);
                String baseName = parts[1].substring(0, suffixIndex - 1); //remove the underscore

                CommonMod.SIXWAY_SLABS.getFromBaseName(baseName).ifPresent(reference -> {
                    if (parentModel instanceof BlockModel) {
                        BlockModel model = createBlockModel((BlockModel) parentModel, slabType);
                        model.name = location.toString();
                        cir.setReturnValue(model);
                    }
                });
            }
        }
    }


    private BlockModel createBlockModel(BlockModel parent, String slabType) {
        Material top, bottom, side;

        if (parent.hasTexture("#all")) {
            top = bottom = side = parent.getMaterial("#all");
        } else if (parent.hasTexture("#end") && parent.hasTexture("#side")) {
            top = bottom = parent.getMaterial("#end");
            side = parent.getMaterial("#side");
        } else if (parent.hasTexture("#top") && parent.hasTexture("#side") && parent.hasTexture("#bottom")) {
            top = parent.getMaterial("#top");
            bottom = parent.getMaterial("#bottom");
            side = parent.getMaterial("#side");
        } else {
            Optional<Material> material = parent.getMaterials(this::getModel, Sets.newLinkedHashSet()).stream().findFirst();
            top = bottom = side = material.orElse(new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));
        }
        return SixwaySlabModelHelper.getReplacedModel(slabType, top.texture(), bottom.texture(), side.texture());
    }

    /**
     * Returns the first model location it can find from the default unbaked model
     */
    private ResourceLocation getDefaultModel(Block block) {
        UnbakedModel model = getDefaultUnbakedModel(block);
        if (model instanceof MultiVariant multiVariant) {
            return multiVariant.getVariants().get(0).getModelLocation();
        } else if (model instanceof MultiPart multiPart) {
            return multiPart.getSelectors().get(0).getVariant().getVariants().get(0).getModelLocation();
        } else if (model instanceof BlockModel blockModel && !blockModel.name.isEmpty()) {
            return new ResourceLocation(blockModel.name);
        }
        return MISSING_MODEL_LOCATION;
    }

    /**
     * gets the UnbakedModel(A MultiVariant/MultiPart) for the default state of the block.
     */
    private UnbakedModel getDefaultUnbakedModel(Block block) {
        BlockState defaultState = block.defaultBlockState();
        if (block instanceof SlabBlock) {
            defaultState = defaultState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
        }
        return this.getModel(BlockModelShaper.stateToModelLocation(defaultState));
    }
}

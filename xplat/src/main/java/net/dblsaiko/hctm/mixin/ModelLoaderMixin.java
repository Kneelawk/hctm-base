package net.dblsaiko.hctm.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.model.ModelLoader;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
//
//    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
//
//    @Shadow @Final private ResourceManager resourceManager;
//
//    private Function2<BlockState, UnbakedModel, UnbakedModel> modelWrapper;
//
//    private Function2<BlockState, UnbakedModel, UnbakedModel> getModelWrapper() {
//        if (modelWrapper == null) {
//            modelWrapper = ModelWrapperHandler.INSTANCE.prepare(resourceManager);
//        }
//        return modelWrapper;
//    }
//
//    @Inject(
//        method = "loadModel(Lnet/minecraft/util/Identifier;)V",
//        at = @At(value = "INVOKE", target = "Ljava/util/Map;putAll(Ljava/util/Map;)V", shift = Shift.BEFORE),
//        locals = LocalCapture.CAPTURE_FAILHARD
//    )
//    private void loadModel(Identifier id, CallbackInfo ci, ModelIdentifier modelIdentifier, Identifier identifier,
//                           StateManager stateManager, List list, ImmutableList immutableList, Map map, Map map2,
//                           Identifier identifier2, UnbakedModel unbakedModel,
//                           @Coerce Object modelDefinition, Pair pair, List list2, Iterator var14,
//                           Pair pair2, ModelVariantMap modelVariantMap, Map<BlockState, Pair<UnbakedModel, Supplier<ModelDefinitionExt>>> map3,
//                           MultipartUnbakedModel multipartUnbakedModel) {
//        map3.keySet().forEach(k -> map3.computeIfPresent(k, (state, entry) -> {
//            UnbakedModel model = getModelWrapper().invoke(state, entry.getFirst());
//            return Pair.of(model, entry.getSecond());
//        }));
//    }
}

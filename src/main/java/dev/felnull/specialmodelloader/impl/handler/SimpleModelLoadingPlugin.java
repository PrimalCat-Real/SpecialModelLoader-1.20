//package dev.felnull.specialmodelloader.impl.handler;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI;
//import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
//import dev.felnull.specialmodelloader.impl.SpecialModelLoader;
//import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
//import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
//import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
//import net.minecraft.client.resources.model.UnbakedModel;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.packs.resources.MultiPackResourceManager;
//import net.minecraft.server.packs.resources.ResourceManager;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import com.google.common.collect.ImmutableSet;
//
//import java.io.Reader;
//import java.util.Set;
//import java.util.regex.Pattern;
//
//public class SimpleModelLoadingPlugin implements ModelLoadingPlugin, ModelModifier.OnLoad  {
//
//    private static final Gson GSON = new Gson();
//    private static final String MANUAL_LOAD_SCOPE_RESOURCE_NAME = "sml_load_scopes";
//    private static final Logger LOGGER = LogManager.getLogger("SimpleModelLoadingPlugin");
//
//    private ResourceManager resourceManager;
//    private Set<Pattern> manualLoadScopePatterns;
//    private ResourceLocation firstLoadId;
//
//
//    public SimpleModelLoadingPlugin(ResourceManager resourceManager) {
//        this.resourceManager = resourceManager;
//    }
//
//
//    // Метод для регистрации плагина в точке входа мода (например, в классе ModInitializer)
//    public static void registerPlugin() {
//        ModelLoadingPlugin.register(pluginContext -> {
//            // ResourceManager access is provided like in the v0 API, but in a central place, and can be used for shared processing in the plugin.
//            ResourceManager manager = pluginContext.resourceManager();
//
//            // Model resource providers still exist, but they are called model resolvers now
//            pluginContext.resolveModel().register(context -> {
//                // ...
//            });
//        });
//
//    }
//    @Override
//    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
//        pluginContext.modifyModelOnLoad().register((model, context) -> modifyModelOnLoad(model, context));
//    }
//
//    @Override
//    public UnbakedModel modifyModelOnLoad(UnbakedModel model, ModelModifier.OnLoad.Context context) {
//        if (checkManualLoadScope(context.resourceId())) {
//            return SpecialModelLoaderAPI.getInstance().loadModel(resourceManager, context.resourceId());
//        }
//        return model;
//    }
//
//    private boolean checkManualLoadScope(ResourceLocation resourceLocation) {
//        if (firstLoadId == null)
//            firstLoadId = resourceLocation;
//
//        if (firstLoadId.equals(resourceLocation)) {
//            ImmutableSet.Builder<Pattern> patterns = new ImmutableSet.Builder<>();
//
//            if(resourceManager != null){
//                resourceManager.listResources(MANUAL_LOAD_SCOPE_RESOURCE_NAME, loc -> loc.getPath().endsWith(".json")).forEach((location, resource) -> {
//                    try (Reader reader = resource.openAsReader()) {
//                        JsonObject jo = GSON.fromJson(reader, JsonObject.class);
//                        loadManualLoadScope(patterns, jo);
//                    } catch (Exception e) {
//                        SpecialModelLoader.LOGGER.error("Error occurred while loading model load scope resource json {}", location, e);
//                    }
//                });
//            }
//
//
//            manualLoadScopePatterns = patterns.build();
//
//            int size = manualLoadScopePatterns.size();
//
//            if (size >= 1)
//                SpecialModelLoader.LOGGER.info("Loaded {} manual model load scope", size);
//        }
//
//        if (manualLoadScopePatterns != null) {
//            String id = resourceLocation.toString();
//
//            return manualLoadScopePatterns.stream()
//                    .anyMatch(pattern -> pattern.matcher(id).matches());
//        }
//
//        return false;
//    }
//
//    private void loadManualLoadScope(ImmutableSet.Builder<Pattern> builder, JsonObject jsonObject) {
//        if (!jsonObject.has("version") || !jsonObject.get("version").isJsonPrimitive() || !jsonObject.getAsJsonPrimitive("version").isNumber())
//            throw new RuntimeException("Unknown version");
//
//        int version = jsonObject.getAsJsonPrimitive("version").getAsInt();
//        if (version != 1)
//            throw new RuntimeException("Unsupported version");
//
//        if (!jsonObject.has("entry") || !jsonObject.get("entry").isJsonArray())
//            return;
//
//        JsonArray entries = jsonObject.getAsJsonArray("entry");
//        for (JsonElement entry : entries) {
//            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString())
//                builder.add(Pattern.compile(entry.getAsString()));
//        }
//    }
//
//
//}

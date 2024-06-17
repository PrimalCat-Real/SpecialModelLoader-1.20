package dev.felnull.specialmodelloader.impl.handler;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI;
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import dev.felnull.specialmodelloader.impl.SpecialModelLoader;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.Reader;
import java.util.Set;
import java.util.regex.Pattern;

public class SMLModelLoadingPlugin implements ModelLoadingPlugin {
    private static final Gson GSON = new Gson();
    private static final String MANUAL_LOAD_SCOPE_RESOURCE_NAME = "sml_load_scopes";
    private final ResourceManager resourceManager;
    private Set<Pattern> manualLoadScopePatterns;
    private ResourceLocation firstLoadId;

    public SMLModelLoadingPlugin(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initManualLoadScopes();
    }

    private void initManualLoadScopes() {
        ImmutableSet.Builder<Pattern> patterns = new ImmutableSet.Builder<>();

        resourceManager.listResources(MANUAL_LOAD_SCOPE_RESOURCE_NAME, loc -> loc.getPath().endsWith(".json")).forEach((location, resource) -> {
            try (Reader reader = resource.openAsReader()) {
                JsonObject jo = GSON.fromJson(reader, JsonObject.class);
                loadManualLoadScope(patterns, jo);
            } catch (Exception e) {
                SpecialModelLoader.LOGGER.error("Error occurred while loading model load scope resource json {}", location, e);
            }
        });

        manualLoadScopePatterns = patterns.build();
        int size = manualLoadScopePatterns.size();
        if (size >= 1)
            SpecialModelLoader.LOGGER.info("Loaded {} manual model load scope", size);
    }


//    @Override
//    public void onInitializeModelLoader(Context context) {
//        context.modifyModelBeforeBake().register((location, model) -> {
//            if (checkManualLoadScope(location) || SpecialModelLoaderEvents.LOAD_SCOPE.invoker().isLoadScope(location)) {
//                // Загрузка модели через API, если она соответствует условиям
//                return SpecialModelLoaderAPI.getInstance().loadModel(resourceManager, location);
//            }
//            return model;
//        });
//    }
// j
    @Override
    public void onInitializeModelLoader(Context context) {
        System.out.println("Info init loader");
        // Регистрируем обработчик модификации моделей перед их "запеканием"
        context.modifyModelBeforeBake().register((model, ctx) -> {
            ResourceLocation location = ctx.resourceId(); // Получаем ResourceLocation из контекста
            if (location == null) {
                location = ctx.topLevelId().id(); // Если resourceId() возвращает null, используем topLevelId()
            }

            // Проверяем, соответствует ли ResourceLocation условиям загрузки специальной модели
            if (location != null && (checkManualLoadScope(location) || SpecialModelLoaderEvents.LOAD_SCOPE.invoker().isLoadScope(location))) {
                // Загружаем и возвращаем модифицированную модель, если это возможно
                UnbakedModel specialModel = SpecialModelLoaderAPI.getInstance().loadModel(resourceManager, location);
                if (specialModel != null) return specialModel; // Возвращаем модифицированную модель, если загрузка удалась
            }
            // Возвращаем неизменённую модель, если условия не выполнены
            return model;
        });
    }



    private boolean checkManualLoadScope(ResourceLocation resourceLocation) {
        String id = resourceLocation.toString();
        return manualLoadScopePatterns != null && manualLoadScopePatterns.stream()
                .anyMatch(pattern -> pattern.matcher(id).matches());
    }

    private void loadManualLoadScope(ImmutableSet.Builder<Pattern> builder, JsonObject jsonObject) {
        if (!jsonObject.has("version") || !jsonObject.get("version").isJsonPrimitive() || !jsonObject.getAsJsonPrimitive("version").isNumber())
            throw new RuntimeException("Unknown version");

        int version = jsonObject.getAsJsonPrimitive("version").getAsInt();
        if (version != 1)
            throw new RuntimeException("Unsupported version");

        if (!jsonObject.has("entry") || !jsonObject.get("entry").isJsonArray())
            return;

        JsonArray entries = jsonObject.getAsJsonArray("entry");
        for (JsonElement entry : entries) {
            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString())
                builder.add(Pattern.compile(entry.getAsString()));
        }
    }
}
package dev.felnull.specialmodelloader.impl.model.obj;

import com.google.gson.JsonObject;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import dev.felnull.specialmodelloader.api.model.obj.ObjModelLoader;
import dev.felnull.specialmodelloader.api.model.obj.ObjModelOption;
import dev.felnull.specialmodelloader.impl.SpecialModelLoader;
//import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObjModelLoaderImp implements ObjModelLoader {

    @Override
    public @Nullable UnbakedModel loadModel(@NotNull ResourceManager resourceManager, @NotNull JsonObject modelJson) throws IllegalStateException {
        if (!modelJson.has("model") || !modelJson.get("model").isJsonPrimitive() || !modelJson.getAsJsonPrimitive("model").isString())
            return null;

        var modelLocation = ResourceLocation.parse(modelJson.get("model").getAsString());
        return loadModel(resourceManager, modelLocation, ObjModelOption.parse(modelJson));
    }

    @Override
    public @Nullable UnbakedModel loadModel(@NotNull ResourceManager resourceManager, @NotNull ResourceLocation location, @NotNull ObjModelOption option) throws IllegalStateException {
        var res = resourceManager.getResource(location);
        if (res.isEmpty())
            return null;

        try (var reader = res.get().openAsReader()) {
            var obj = ObjUtils.convertToRenderable(ObjReader.read(reader));

            String[] paths = location.getPath().split("/");
            paths = ArrayUtils.remove(paths, paths.length - 1);
            var loc = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), String.join("/", paths));

            return new ObjUnbakedModelModel(obj, loadMtl(resourceManager, loc, obj.getMtlFileNames()), option);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load obj file", e);
        }
    }

    private Map<String, Mtl> loadMtl(ResourceManager resourceManager, ResourceLocation location, List<String> mtlNames) {
        return mtlNames.stream().flatMap(r -> loadMtl(resourceManager, location, r).stream()).collect(Collectors.toMap(Mtl::getName, r -> r));
    }

    private List<Mtl> loadMtl(ResourceManager resourceManager, ResourceLocation location, String mtlName) {
        var loc = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "/" + mtlName);
        return resourceManager.getResource(loc).map(res -> {
            try (var reader = res.openAsReader()) {
                return MtlReader.read(reader);
            } catch (IOException e) {
                SpecialModelLoader.LOGGER.error("Failed to read mtl file.", e);
                return new ArrayList<Mtl>();
            }
        }).orElseGet(List::of);
    }

    @Override
    public String getId() {
        return "obj";
    }
}

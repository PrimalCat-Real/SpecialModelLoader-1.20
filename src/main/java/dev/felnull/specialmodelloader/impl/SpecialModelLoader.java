package dev.felnull.specialmodelloader.impl;

//import dev.felnull.specialmodelloader.impl.handler.SMLModelLoadingPlugin;
//import dev.felnull.specialmodelloader.impl.handler.SMLModelLoadingPluginTest;
import dev.felnull.specialmodelloader.impl.handler.SMLModelResourceHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpecialModelLoader implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(SpecialModelLoader.class);
    public static final String MODID = "special-model-loader";

    @Override
    public void onInitializeClient() {
        SMLModelResourceHandler.init();
    }
}

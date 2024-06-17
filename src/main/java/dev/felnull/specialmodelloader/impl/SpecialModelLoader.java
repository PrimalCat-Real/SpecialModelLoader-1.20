package dev.felnull.specialmodelloader.impl;

import dev.felnull.specialmodelloader.impl.handler.SMLModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpecialModelLoader implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(SpecialModelLoader.class);
    public static final String MODID = "special-model-loader";

    @Override
    public void onInitializeClient() {
//        SMLModelLoadingPlugin.init();
//        ModelLoadingPluginManager.registerPlugin(new SMLModelLoadingPlugin());
    }
}

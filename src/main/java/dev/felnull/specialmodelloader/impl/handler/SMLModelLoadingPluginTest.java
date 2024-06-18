//package dev.felnull.specialmodelloader.impl.handler;
//
//import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
//
//public class SMLModelLoadingPluginTest implements ModelLoadingPlugin {
//
//    public SMLModelLoadingPluginTest(ModelLoadingPlugin.Context context) {
//    }
//
//    public static void init() {
//        System.out.println("init modloader");
//        ModelLoadingPlugin.register(SMLModelLoadingPluginTest::new);
//    }
////    @Override
////    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
////
////        return;
////    }
//
//    @Override
//    public void onInitializeModelLoader(Object data, ModelLoadingPlugin.Context pluginContext) {
//        System.out.println("download models " + pluginContext.toString());
//    }
//}

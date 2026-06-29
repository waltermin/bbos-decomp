package net.rim.plazmic.internal.mediaengine.module;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.registry.Registry;

class ModelIntarray_v0_0 {
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v0_0$AnimationSampler;

   public static void libMain(String[] args) {
      Registry registry = MediaFactory.getRegistry();
      String modelClassName = "net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.AnimationModel";
      registry.setValue(new Object[]{"MEDIA", modelClassName}, "1");
      registry.setValue(new Object[]{"MEDIA", modelClassName, "Viewport"}, "net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.AnimationViewport");
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "EventSubscription"}, "net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.AnimationInteractor"
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "FocusInteractor"}, "net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.AnimationInteractor"
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "Sampler"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v0_0$AnimationSampler == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v0_0$AnimationSampler = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.AnimationSampler"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v0_0$AnimationSampler)
            .getName()
      );
      registry.setValue(
         new String[]{"CONTENT", "application/x-vnd.rim.pme", "0.2.0.0"}, "net.rim.plazmic.internal.mediaengine.model.intarray.v0_0.PME0_2Reader"
      );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}

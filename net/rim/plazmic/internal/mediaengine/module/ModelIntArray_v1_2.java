package net.rim.plazmic.internal.mediaengine.module;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.registry.Registry;

class ModelIntArray_v1_2 {
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader;
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl;
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$AnimationViewport;
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PMEGraphicsViewport;
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$FocusHandler;
   static Class class$net$rim$plazmic$internal$mediaengine$event$EventResolverImpl;
   static Class class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$BehaviorManager;
   static Class class$net$rim$plazmic$internal$mediaengine$ui$PME12GraphicsImpl;

   public static void libMain(String[] args) {
      Registry registry = MediaFactory.getRegistry();
      registry.setValue(
         new String[]{"CONTENT", "application/x-vnd.rim.pme", "1.0.0.0"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.PME_Reader"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader)
            .getName()
      );
      registry.setValue(
         new String[]{"CONTENT", "application/x-vnd.rim.pme", "1.1.0.0"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.PME_Reader"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader)
            .getName()
      );
      registry.setValue(
         new String[]{"CONTENT", "application/x-vnd.rim.pme", "1.2.0.0"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.PME_Reader"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PME_Reader)
            .getName()
      );
      String modelClassName = (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl == null
            ? (
               class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl = class$(
                  "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl"
               )
            )
            : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl)
         .getName();
      registry.setValue(new Object[]{"MEDIA", modelClassName}, "1");
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "Viewport"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$AnimationViewport == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$AnimationViewport = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.AnimationViewport"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$AnimationViewport)
            .getName()
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "PMEViewport"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PMEGraphicsViewport == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PMEGraphicsViewport = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.PMEGraphicsViewport"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$PMEGraphicsViewport)
            .getName()
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "EventSubscription"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl)
            .getName()
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "ModelInteractor"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$ModelInteractorImpl)
            .getName()
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "FocusInteractor"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$FocusHandler == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$FocusHandler = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.FocusHandler"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$FocusHandler)
            .getName()
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "EventResolver"},
         (class$net$rim$plazmic$internal$mediaengine$event$EventResolverImpl == null
               ? (class$net$rim$plazmic$internal$mediaengine$event$EventResolverImpl = class$("net.rim.plazmic.internal.mediaengine.event.EventResolverImpl"))
               : class$net$rim$plazmic$internal$mediaengine$event$EventResolverImpl)
            .getName()
      );
      registry.setValue(
         new Object[]{"MEDIA", modelClassName, "MAIN_SERVICE"},
         (class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$BehaviorManager == null
               ? (
                  class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$BehaviorManager = class$(
                     "net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.BehaviorManager"
                  )
               )
               : class$net$rim$plazmic$internal$mediaengine$model$intarray$v1_2$BehaviorManager)
            .getName()
      );
      registry.setValue(
         new String[]{"FRAMEWORK", "GRAPHICS"},
         (class$net$rim$plazmic$internal$mediaengine$ui$PME12GraphicsImpl == null
               ? (class$net$rim$plazmic$internal$mediaengine$ui$PME12GraphicsImpl = class$("net.rim.plazmic.internal.mediaengine.ui.PME12GraphicsImpl"))
               : class$net$rim$plazmic$internal$mediaengine$ui$PME12GraphicsImpl)
            .getName()
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

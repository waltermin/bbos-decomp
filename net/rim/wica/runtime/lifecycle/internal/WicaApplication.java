package net.rim.wica.runtime.lifecycle.internal;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.UiApplication;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.service.DefaultContainer;

public final class WicaApplication extends UiApplication {
   private DefaultContainer _container;
   private WicletImpl _model;
   private WicletRuntime _runtime;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$event$internal$EventServiceImpl;
   static Class class$net$rim$wica$runtime$metadata$internal$WicletRuntimeImpl;
   static Class class$net$rim$wica$runtime$script$ScriptEngine;
   static Class class$net$rim$wica$runtime$script$internal$ScriptEngineImpl;
   static Class class$net$rim$wica$runtime$ui$UiService;
   static Class class$net$rim$wica$runtime$ui$internal$UiServiceImpl;
   static Class class$net$rim$wica$runtime$access$data$AccessDataService;
   static Class class$net$rim$wica$runtime$access$internal$data$AccessDataServiceImpl;
   static Class class$net$rim$wica$runtime$access$invoker$AccessInvokeService;
   static Class class$net$rim$wica$runtime$access$internal$invoker$AccessInvokeServiceImpl;

   WicaApplication(WicletImpl model) {
      this._model = model;
      this.enableKeyUpEvents(true);
      Locale.setDefault(model.getDefaultLocale());
      this._container = new DefaultContainer(this._model.getServiceProvider());
      this.registerComponents();
   }

   final void start() {
      try {
         this._runtime = (WicletRuntime)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$WicletRuntime == null
                  ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
                  : class$net$rim$wica$runtime$metadata$WicletRuntime
            );
         this._runtime.start(this._model);
         this.enterEventDispatcher();
      } finally {
         this._model.stopStarted();
         this._model.stopCompleted();
         return;
      }
   }

   final void stop(boolean forceful) {
      this._runtime.stop(false, forceful);
   }

   final void shutdown() {
      label17:
      try {
         this._container.stop();
      } finally {
         break label17;
      }

      System.exit(0);
   }

   private final void registerComponents() {
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService,
            class$net$rim$wica$runtime$event$internal$EventServiceImpl == null
               ? (class$net$rim$wica$runtime$event$internal$EventServiceImpl = class$("net.rim.wica.runtime.event.internal.EventServiceImpl"))
               : class$net$rim$wica$runtime$event$internal$EventServiceImpl
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$metadata$WicletRuntime == null
               ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
               : class$net$rim$wica$runtime$metadata$WicletRuntime,
            class$net$rim$wica$runtime$metadata$internal$WicletRuntimeImpl == null
               ? (class$net$rim$wica$runtime$metadata$internal$WicletRuntimeImpl = class$("net.rim.wica.runtime.metadata.internal.WicletRuntimeImpl"))
               : class$net$rim$wica$runtime$metadata$internal$WicletRuntimeImpl
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$script$ScriptEngine == null
               ? (class$net$rim$wica$runtime$script$ScriptEngine = class$("net.rim.wica.runtime.script.ScriptEngine"))
               : class$net$rim$wica$runtime$script$ScriptEngine,
            class$net$rim$wica$runtime$script$internal$ScriptEngineImpl == null
               ? (class$net$rim$wica$runtime$script$internal$ScriptEngineImpl = class$("net.rim.wica.runtime.script.internal.ScriptEngineImpl"))
               : class$net$rim$wica$runtime$script$internal$ScriptEngineImpl
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$ui$UiService == null
               ? (class$net$rim$wica$runtime$ui$UiService = class$("net.rim.wica.runtime.ui.UiService"))
               : class$net$rim$wica$runtime$ui$UiService,
            class$net$rim$wica$runtime$ui$internal$UiServiceImpl == null
               ? (class$net$rim$wica$runtime$ui$internal$UiServiceImpl = class$("net.rim.wica.runtime.ui.internal.UiServiceImpl"))
               : class$net$rim$wica$runtime$ui$internal$UiServiceImpl
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$access$data$AccessDataService == null
               ? (class$net$rim$wica$runtime$access$data$AccessDataService = class$("net.rim.wica.runtime.access.data.AccessDataService"))
               : class$net$rim$wica$runtime$access$data$AccessDataService,
            class$net$rim$wica$runtime$access$internal$data$AccessDataServiceImpl == null
               ? (
                  class$net$rim$wica$runtime$access$internal$data$AccessDataServiceImpl = class$(
                     "net.rim.wica.runtime.access.internal.data.AccessDataServiceImpl"
                  )
               )
               : class$net$rim$wica$runtime$access$internal$data$AccessDataServiceImpl
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$access$invoker$AccessInvokeService == null
               ? (class$net$rim$wica$runtime$access$invoker$AccessInvokeService = class$("net.rim.wica.runtime.access.invoker.AccessInvokeService"))
               : class$net$rim$wica$runtime$access$invoker$AccessInvokeService,
            class$net$rim$wica$runtime$access$internal$invoker$AccessInvokeServiceImpl == null
               ? (
                  class$net$rim$wica$runtime$access$internal$invoker$AccessInvokeServiceImpl = class$(
                     "net.rim.wica.runtime.access.internal.invoker.AccessInvokeServiceImpl"
                  )
               )
               : class$net$rim$wica$runtime$access$internal$invoker$AccessInvokeServiceImpl
         );
   }

   public final EventService getEventService() {
      return (EventService)this._container
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
   }

   @Override
   public final String toString() {
      return this._model.getName();
   }

   @Override
   public final void activate() {
      this._model.onActivate();
      this._runtime.activate();
   }

   @Override
   public final void deactivate() {
      this._runtime.deactivate();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}

package net.rim.wica.runtime.metadata.internal;

import net.rim.wica.runtime.access.data.AccessDataService;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.ui.ScreenManager;
import net.rim.wica.runtime.metadata.internal.handler.MsgHandler;
import net.rim.wica.runtime.metadata.internal.handler.UIHandler;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;
import net.rim.wica.runtime.script.ScriptEngine;
import net.rim.wica.runtime.service.ComponentAdapter;
import net.rim.wica.runtime.service.Container;
import net.rim.wica.runtime.service.DefaultComponentAdapter;
import net.rim.wica.runtime.service.DefaultContainer;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.ui.UiService;
import net.rim.wica.runtime.util.internal.BackgroundWorker;

public final class WicletRuntimeImpl implements WicletRuntime, Serviceable {
   private WicletImpl _app;
   private Container _container;
   private boolean _started;
   private boolean _shuttingDown;
   private BackgroundWorker _worker = new BackgroundWorker("Background Worker");
   private EventService _eventService;
   private UiService _uiService;
   private ScriptEngine _scriptEngine;
   private MsgHandler _msgHandler;
   private boolean _active;
   private Object _startupLock = new Object();
   static Class class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler;
   static Class class$net$rim$wica$runtime$metadata$internal$handler$UIHandler;
   static Class class$net$rim$wica$runtime$metadata$component$ui$ScreenManager;
   static Class class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;
   static Class class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;
   static Class class$net$rim$wica$runtime$ui$UiService;
   static Class class$net$rim$wica$runtime$script$ScriptEngine;
   static Class class$net$rim$wica$runtime$access$data$AccessDataService;
   static Class class$net$rim$wica$runtime$event$EventService;

   private final void initServices() {
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler = class$("net.rim.wica.runtime.metadata.internal.handler.ErrorHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler,
            class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler = class$("net.rim.wica.runtime.metadata.internal.handler.ErrorHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler
         );
      if (this._app.hasUI()) {
         ComponentAdapter uiAdapter = new DefaultComponentAdapter(
            class$net$rim$wica$runtime$metadata$internal$handler$UIHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$UIHandler = class$("net.rim.wica.runtime.metadata.internal.handler.UIHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$UIHandler
         );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$metadata$component$ui$ScreenManager == null
                  ? (class$net$rim$wica$runtime$metadata$component$ui$ScreenManager = class$("net.rim.wica.runtime.metadata.component.ui.ScreenManager"))
                  : class$net$rim$wica$runtime$metadata$component$ui$ScreenManager,
               uiAdapter
            );
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$metadata$internal$handler$UIHandler == null
                  ? (class$net$rim$wica$runtime$metadata$internal$handler$UIHandler = class$("net.rim.wica.runtime.metadata.internal.handler.UIHandler"))
                  : class$net$rim$wica$runtime$metadata$internal$handler$UIHandler,
               uiAdapter
            );
      }

      if (this._app.hasMessages()) {
         this._container
            .registerComponent(
               class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler == null
                  ? (class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler = class$("net.rim.wica.runtime.metadata.internal.handler.MsgHandler"))
                  : class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler,
               class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler == null
                  ? (class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler = class$("net.rim.wica.runtime.metadata.internal.handler.MsgHandler"))
                  : class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler
            );
      }

      this._container
         .registerComponent(
            class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                     "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager,
            class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                     "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler = class$("net.rim.wica.runtime.metadata.internal.handler.KeylessHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler,
            class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler = class$("net.rim.wica.runtime.metadata.internal.handler.KeylessHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler
         );
      this._container
         .registerComponent(
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener,
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
         );
      this._container
         .getService(
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
         );
      this._container
         .getService(
            class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler = class$("net.rim.wica.runtime.metadata.internal.handler.KeylessHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler
         );
      this._container
         .getService(
            class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler = class$("net.rim.wica.runtime.metadata.internal.handler.ErrorHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$ErrorHandler
         );
      if (this._app.hasMessages()) {
         this._msgHandler = (MsgHandler)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler == null
                  ? (class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler = class$("net.rim.wica.runtime.metadata.internal.handler.MsgHandler"))
                  : class$net$rim$wica$runtime$metadata$internal$handler$MsgHandler
            );
      }

      this._uiService = (UiService)this._container
         .getService(
            class$net$rim$wica$runtime$ui$UiService == null
               ? (class$net$rim$wica$runtime$ui$UiService = class$("net.rim.wica.runtime.ui.UiService"))
               : class$net$rim$wica$runtime$ui$UiService
         );
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void callEntryPoint() {
      this._scriptEngine = (ScriptEngine)this._container
         .getService(
            class$net$rim$wica$runtime$script$ScriptEngine == null
               ? (class$net$rim$wica$runtime$script$ScriptEngine = class$("net.rim.wica.runtime.script.ScriptEngine"))
               : class$net$rim$wica$runtime$script$ScriptEngine
         );

      try {
         this._scriptEngine.startEngine(new WicletRuntimeImpl$ScriptMonitorImpl(this, null));
      } catch (Throwable var10) {
         throw new Object(((StringBuffer)(new Object("ScriptEngine: "))).append(e.getMessage()).toString());
      }

      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         int entryDef = this._app.getContext().getEntryPoint();
         if (entryDef != -1) {
            switch (this._app.getDefType(entryDef)) {
               case 9:
                  var6 = false;
                  break;
               case 10:
                  ScreenManager ui = (ScreenManager)this._container
                     .getService(
                        class$net$rim$wica$runtime$metadata$component$ui$ScreenManager == null
                           ? (
                              class$net$rim$wica$runtime$metadata$component$ui$ScreenManager = class$(
                                 "net.rim.wica.runtime.metadata.component.ui.ScreenManager"
                              )
                           )
                           : class$net$rim$wica$runtime$metadata$component$ui$ScreenManager
                     );
                  ui.display(this._app.getScreenModel(entryDef));
                  var6 = false;
                  break;
               case 11:
               default:
                  this._scriptEngine.call(entryDef, (long[])null);
                  var6 = false;
            }
         } else {
            var6 = false;
         }
      } finally {
         if (var6) {
            this.notifyStartupLock();
         }
      }

      this.notifyStartupLock();
   }

   private final void activateInternal() {
      this._active = true;
      if (this._started) {
         PersistenceListener psListener = (PersistenceListener)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                        "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
            );
         psListener.activate();
         AccessDataService accessService = (AccessDataService)this._container
            .getService(
               class$net$rim$wica$runtime$access$data$AccessDataService == null
                  ? (class$net$rim$wica$runtime$access$data$AccessDataService = class$("net.rim.wica.runtime.access.data.AccessDataService"))
                  : class$net$rim$wica$runtime$access$data$AccessDataService
            );
         accessService.activate();
         UIHandler uiHandler = (UIHandler)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$handler$UIHandler == null
                  ? (class$net$rim$wica$runtime$metadata$internal$handler$UIHandler = class$("net.rim.wica.runtime.metadata.internal.handler.UIHandler"))
                  : class$net$rim$wica$runtime$metadata$internal$handler$UIHandler
            );
         if (uiHandler != null) {
            uiHandler.activate();
         }
      }

      if (this._msgHandler != null) {
         this._msgHandler.activate();
      }

      this._started = true;
   }

   private final void deactivateInternal() {
      this._active = false;
      if (!this._shuttingDown) {
         if (this._msgHandler != null) {
            this._msgHandler.deactivate();
         }

         UIHandler uiHandler = (UIHandler)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$handler$UIHandler == null
                  ? (class$net$rim$wica$runtime$metadata$internal$handler$UIHandler = class$("net.rim.wica.runtime.metadata.internal.handler.UIHandler"))
                  : class$net$rim$wica$runtime$metadata$internal$handler$UIHandler
            );
         if (uiHandler != null) {
            uiHandler.deactivate();
         }

         this._app.save();
         AccessDataService accessService = (AccessDataService)this._container
            .getService(
               class$net$rim$wica$runtime$access$data$AccessDataService == null
                  ? (class$net$rim$wica$runtime$access$data$AccessDataService = class$("net.rim.wica.runtime.access.data.AccessDataService"))
                  : class$net$rim$wica$runtime$access$data$AccessDataService
            );
         accessService.deactivate();
         PersistenceListener psListener = (PersistenceListener)this._container
            .getService(
               class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
                  ? (
                     class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                        "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                     )
                  )
                  : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
            );
         psListener.deactivate();
      }
   }

   @Override
   public final void start(WicletContext context) {
      this._app = new WicletImpl(context, this);
      this.requestTask(1, null);
      this.initServices();
      synchronized (this._startupLock) {
         this._worker.start();

         try {
            this._startupLock.wait();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void notifyStartupLock() {
      synchronized (this._startupLock) {
         this._startupLock.notifyAll();
      }
   }

   @Override
   public final void stop() {
      this.stop(false, false);
   }

   @Override
   public final synchronized void stop(boolean error, boolean forceful) {
      if (!this._shuttingDown) {
         this._shuttingDown = true;
         this._app.getContext().stopStarted();
         if (forceful || error) {
            this._scriptEngine.stopExecution();
            this._uiService.closeModalDialog();
         }

         this.requestExclusiveTask(7, new WicletRuntimeImpl$StopRunnable(this, error));
      }
   }

   @Override
   public final boolean isStarted() {
      return this._started;
   }

   @Override
   public final boolean isActive() {
      return this._active;
   }

   @Override
   public final void activate() {
      this.requestTask(2, null);
   }

   @Override
   public final void deactivate() {
      this.requestTask(3, null);
   }

   @Override
   public final void requestScreenBack() {
      this.requestTask(6, null);
   }

   @Override
   public final void requestProcessMessage() {
      this.requestTask(4, null);
   }

   @Override
   public final void requestMenuShow(int instance) {
      this.requestTask(5, new Object(instance));
   }

   @Override
   public final void enqueueRunnable(Runnable runnable) {
      this.requestTask(8, runnable);
   }

   @Override
   public final void enqueuePriorityRunnable(Runnable runnable) {
      this.requestPriorityTask(8, runnable);
   }

   @Override
   public final Wiclet getWiclet() {
      return this._app;
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      this._container = new DefaultContainer(provider);
      this._eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
   }

   @Override
   public final Object getService(Class serviceInterface) {
      return this._container.getService(serviceInterface);
   }

   private final void requestExclusiveTask(int task, Object context) {
      this._worker.addExclusiveToQueue(new WicletRuntimeImpl$ProcessingRequest(this, task, context, null));
   }

   private final void requestTask(int task, Object context) {
      this._worker.addToQueue(new WicletRuntimeImpl$ProcessingRequest(this, task, context, null));
   }

   private final void requestPriorityTask(int task, Object context) {
      this._worker.addPriorityToQueue(new WicletRuntimeImpl$ProcessingRequest(this, task, context, null));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}

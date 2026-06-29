package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.ui.UiApplication;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class CallHandlerFactory {
   private boolean _initialized = false;
   private CallHandler _handlerForUpdate;
   private CollectionListenerFactoryHelper _helper = new CollectionListenerFactoryHelper();
   private static CallHandlerFactory _factory;

   public static CallHandlerFactory getFactory() {
      if (_factory == null) {
         _factory = new CallHandlerFactory();
      }

      return _factory;
   }

   private CallHandlerFactory() {
      Thread thread = (Thread)(new Object(new CallHandlerFactory$1(this)));
      thread.setPriority(1);
      thread.start();
   }

   public CallHandler createInstance(ModelInteractorImpl mi, UiApplication app) {
      CallHandler instance = new CallHandler(mi, app);
      this._helper.addComponentForUpdate(instance);
      synchronized (this._helper) {
         if (this._initialized) {
            instance.updateLater();
         } else {
            this._handlerForUpdate = instance;
         }

         return instance;
      }
   }
}

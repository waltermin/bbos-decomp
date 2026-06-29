package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.system.MMS;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ribbon.skin.svg.NewMessageFilter;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class SMSHandlerFactory {
   private CollectionListenerFactoryHelper _helper;
   private NewMessageFilter _collection;
   private boolean _initialized = false;
   private SMSHandler _handlerForUpdate;
   private static SMSHandlerFactory _factory;

   public static SMSHandlerFactory getFactory() {
      if (_factory == null) {
         _factory = new SMSHandlerFactory();
      }

      return _factory;
   }

   private SMSHandlerFactory() {
      this._helper = new CollectionListenerFactoryHelper();
      Thread thread = (Thread)(new Object(new SMSHandlerFactory$1(this)));
      thread.setPriority(1);
      thread.start();
   }

   public SMSHandler createInstance(ModelInteractorImpl mi, UiApplication app) {
      SMSHandler instance = new SMSHandler(mi, app, this._collection);
      synchronized (this._helper) {
         if (this._initialized) {
            instance._collection = this._collection;
            MMS.onEnabled(instance);
            instance.updateLater();
         } else {
            this._handlerForUpdate = instance;
         }
      }

      this._helper.addComponentForUpdate(instance);
      return instance;
   }
}

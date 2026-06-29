package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ribbon.components.GlobalListenerFactoryHelper;
import net.rim.device.apps.internal.ribbon.skin.svg.NewMessageFilter;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class EmailHandlerFactory {
   private CollectionListenerFactoryHelper _collectionEventHelper;
   private GlobalListenerFactoryHelper _globalEventHelper;
   private NewMessageFilter _collection;
   private boolean _initialized = false;
   private EmailHandler _handlerForUpdate;
   private static EmailHandlerFactory _factory;

   public static EmailHandlerFactory getFactory() {
      if (_factory == null) {
         _factory = new EmailHandlerFactory();
      }

      return _factory;
   }

   private EmailHandlerFactory() {
      this._globalEventHelper = new GlobalListenerFactoryHelper();
      Application.getApplication().addGlobalEventListener(this._globalEventHelper);
      this._collectionEventHelper = new CollectionListenerFactoryHelper();
      Thread thread = new Thread(new EmailHandlerFactory$1(this));
      thread.setPriority(1);
      thread.start();
   }

   public EmailHandler createInstance(ModelInteractorImpl mi, UiApplication app) {
      EmailHandler instance = new EmailHandler(mi, app, this._collection);
      synchronized (this._collectionEventHelper) {
         if (this._initialized) {
            instance._collection = this._collection;
            instance.updateLater();
         } else {
            this._handlerForUpdate = instance;
         }
      }

      this._collectionEventHelper.addComponentForUpdate(instance);
      this._globalEventHelper.addComponentForUpdate(instance);
      return instance;
   }
}

package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ribbon.components.RealtimeClockListenerFactoryHelper;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class CalendarHandlerFactory {
   private CollectionListenerFactoryHelper _helper;
   private RealtimeClockListenerFactoryHelper _rtClockHelper;
   private boolean _initialized = false;
   private CalendarHandler _handlerForUpdate;
   private static CalendarHandlerFactory _factory;

   public static CalendarHandlerFactory getFactory() {
      if (_factory == null) {
         _factory = new CalendarHandlerFactory();
      }

      return _factory;
   }

   private CalendarHandlerFactory() {
      this._helper = new CollectionListenerFactoryHelper();
      this._rtClockHelper = new RealtimeClockListenerFactoryHelper();
      Thread thread = new Thread(new CalendarHandlerFactory$1(this));
      thread.setPriority(1);
      thread.start();
   }

   public CalendarHandler createInstance(ModelInteractorImpl mi, UiApplication app) {
      CalendarHandler instance = new CalendarHandler(mi, app);
      this._helper.addComponentForUpdate(instance);
      this._rtClockHelper.addComponentForUpdate(instance);
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

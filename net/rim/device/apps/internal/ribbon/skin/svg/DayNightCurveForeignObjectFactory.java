package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.ribbon.components.GlobalListenerFactoryHelper;
import net.rim.device.apps.internal.ribbon.components.RealtimeClockListenerFactoryHelper;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class DayNightCurveForeignObjectFactory {
   private RealtimeClockListenerFactoryHelper _rtClockHelper;
   private static DayNightCurveForeignObjectFactory _factory;
   private static GlobalListenerFactoryHelper _helper;

   public static DayNightCurveForeignObjectFactory getFactory() {
      if (_factory == null) {
         _factory = new DayNightCurveForeignObjectFactory();
      }

      return _factory;
   }

   private DayNightCurveForeignObjectFactory() {
      _helper = new GlobalListenerFactoryHelper();
      this._rtClockHelper = new RealtimeClockListenerFactoryHelper();
      Application.getApplication().addGlobalEventListener(_helper);
      Application.getApplication().addRealtimeClockListener(this._rtClockHelper);
   }

   public DayNightCurveForeignObject createInstance(ModelInteractorImpl mi, String query) {
      DayNightCurveForeignObject instance = new DayNightCurveForeignObject(mi, query);
      _helper.addComponentForUpdate(instance);
      this._rtClockHelper.addComponentForUpdate(instance);
      return instance;
   }
}

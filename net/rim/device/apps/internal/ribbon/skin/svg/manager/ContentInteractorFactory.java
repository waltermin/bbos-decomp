package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.ribbon.components.GlobalListenerFactoryHelper;
import net.rim.device.apps.internal.ribbon.components.SystemListenerFactoryHelper;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;

final class ContentInteractorFactory {
   private static ContentInteractorFactory _factory;
   private static GlobalListenerFactoryHelper _helper;
   private static SystemListenerFactoryHelper _systemHelper;

   public static final ContentInteractorFactory getFactory() {
      if (_factory == null) {
         _factory = new ContentInteractorFactory();
      }

      return _factory;
   }

   private ContentInteractorFactory() {
      _helper = new GlobalListenerFactoryHelper();
      _systemHelper = new SystemListenerFactoryHelper();
      Application.getApplication().addGlobalEventListener(_helper);
      Application.getApplication().addSystemListener(_systemHelper);
   }

   public final ContentInteractorManager createInstance(FocusInteractor fi, ModelInteractorImpl mi) {
      ContentInteractorManager instance = new ContentInteractorManager(fi, mi);
      _helper.addComponentForUpdate(instance);
      _systemHelper.addComponentForUpdate(instance);
      return instance;
   }
}

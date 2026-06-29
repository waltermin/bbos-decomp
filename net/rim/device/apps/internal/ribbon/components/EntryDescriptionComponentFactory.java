package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;

final class EntryDescriptionComponentFactory implements Factory {
   private EntryComponentFactoryHelper _helper = new EntryComponentFactoryHelper();
   private GlobalListenerFactoryHelper _globalEventHelper = new GlobalListenerFactoryHelper();

   final void init() {
      HierarchyManager.getInstance().addEntryChangeListener(this._helper);
      Application.getApplication().addGlobalEventListener(this._globalEventHelper);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("EntryDescription", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      EntryDescriptionComponentFactory$EntryDescription instance = new EntryDescriptionComponentFactory$EntryDescription();
      this._helper.addComponentForUpdate(instance);
      this._globalEventHelper.addComponentForUpdate(instance);
      return instance;
   }
}

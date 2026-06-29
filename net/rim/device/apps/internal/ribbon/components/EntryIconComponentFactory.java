package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;

final class EntryIconComponentFactory implements Factory {
   private EntryComponentFactoryHelper _helper = new EntryComponentFactoryHelper();

   final void init() {
      HierarchyManager.getInstance().addEntryChangeListener(this._helper);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("EntryIcon", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      EntryIconComponentFactory$EntryIconComponent instance = new EntryIconComponentFactory$EntryIconComponent();
      this._helper.addComponentForUpdate(instance);
      return instance;
   }
}

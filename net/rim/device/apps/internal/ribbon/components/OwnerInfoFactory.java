package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

final class OwnerInfoFactory implements Factory, GlobalEventListener {
   private GlobalListenerFactoryHelper _helper = new GlobalListenerFactoryHelper();

   final void init() {
      Application app = Application.getApplication();
      app.addGlobalEventListener(this);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("OwnerInfo", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      OwnerInfoFactory$OwnerField instance = new OwnerInfoFactory$OwnerField();
      this._helper.addComponentForUpdate(instance);
      return instance;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L || guid == -3297167379286550693L) {
         this._helper.eventOccurred(guid, data0, data1, object0, object1);
      }
   }
}

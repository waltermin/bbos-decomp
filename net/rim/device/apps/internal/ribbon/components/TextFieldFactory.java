package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

final class TextFieldFactory implements Factory, GlobalEventListener {
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();

   final void init() {
      Application app = Application.getApplication();
      app.addGlobalEventListener(this);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("Text", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      TextFieldFactory$TextField instance = new TextFieldFactory$TextField();
      this._helper.addComponentForUpdate(instance);
      return instance;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this._helper.doUpdates();
      }
   }
}

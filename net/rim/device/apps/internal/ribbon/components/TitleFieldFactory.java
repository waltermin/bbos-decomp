package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

public final class TitleFieldFactory implements Factory {
   public static final int TITLE_ID = "title".hashCode();

   final void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("Title", this);
   }

   @Override
   public final Object createInstance(Object initialData) {
      return new TitleFieldFactory$TitleField();
   }
}

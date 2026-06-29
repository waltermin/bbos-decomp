package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

public class ZenPlusSkinManagerFactory implements Factory {
   public static void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(3856995967469138522L);
      repos.addFactory("Zen+", new ZenPlusSkinManagerFactory());
   }

   @Override
   public Object createInstance(Object initialData) {
      return new ZenPlusSkinManager();
   }
}

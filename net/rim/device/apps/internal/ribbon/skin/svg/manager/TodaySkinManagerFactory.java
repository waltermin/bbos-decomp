package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

public class TodaySkinManagerFactory implements Factory {
   public static void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(3856995967469138522L);
      repos.addFactory("Today", new TodaySkinManagerFactory());
   }

   @Override
   public Object createInstance(Object initialData) {
      return new TodaySkinManager();
   }
}

package net.rim.device.apps.api.ribbon;

import net.rim.device.api.util.Factory;

public interface RibbonComponentFactoryRepository {
   void addFactory(String var1, Factory var2);

   void removeFactory(String var1);

   Factory getFactory(String var1);
}

package net.rim.device.api.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RuntimeStore;

public final class FactoryUtil {
   private FactoryUtil() {
   }

   public static final Object createInstance(long guid, Object initialData) {
      if (guid != 0 && guid != -1 && guid != Long.MAX_VALUE && guid != Long.MIN_VALUE) {
         Factory f = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(guid);
         if (f == null) {
            f = (Factory)RuntimeStore.getRuntimeStore().waitFor(guid);
         }

         return f.createInstance(initialData);
      } else {
         return null;
      }
   }
}

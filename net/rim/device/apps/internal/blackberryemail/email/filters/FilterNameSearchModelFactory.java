package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class FilterNameSearchModelFactory extends RIMModelFactory {
   public static final long REGKEY = -4993125662867338663L;
   private static FilterNameSearchModelFactory _factory;

   public static final FilterNameSearchModelFactory getInstance() {
      if (_factory == null) {
         _factory = (FilterNameSearchModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-4993125662867338663L);
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 33) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType(true) == 1;
      } else {
         return o instanceof FilterNameSearchModel;
      }
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Object createInstance(Object context) {
      return new FilterNameSearchModel();
   }
}

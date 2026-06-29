package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class ServiceSearchModelFactory extends RIMModelFactory {
   private static final long REGKEY = -6430076058394911877L;
   private static ServiceSearchModelFactory _factory;

   private ServiceSearchModelFactory() {
   }

   public static final ServiceSearchModelFactory getInstance() {
      if (_factory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _factory = (ServiceSearchModelFactory)ar.getOrWaitFor(-6430076058394911877L);
         if (_factory == null) {
            _factory = new ServiceSearchModelFactory();
            ar.put(-6430076058394911877L, _factory);
         }
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb == null ? false : sb.getFieldType() == 14;
      } else {
         return o instanceof ServiceSearchModel;
      }
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Object createInstance(Object context) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(context, 255);
         if (sb != null) {
            try {
               int uidHash = sb.getInt(14, true);
               ServiceSearchModel m = new ServiceSearchModel();
               m._values[1] = uidHash;
               if (sb.containsType(15)) {
                  m._values[0] = sb.getInt(15, true);
               }

               return m;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new ServiceSearchModel();
      }
   }
}

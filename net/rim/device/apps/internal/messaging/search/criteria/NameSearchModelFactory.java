package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class NameSearchModelFactory extends RIMModelFactory {
   private static final long REGKEY = 3949118105778132734L;
   private static NameSearchModelFactory _factory;

   private NameSearchModelFactory() {
   }

   public static final NameSearchModelFactory getInstance() {
      if (_factory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _factory = (NameSearchModelFactory)ar.getOrWaitFor(3949118105778132734L);
         if (_factory == null) {
            _factory = new NameSearchModelFactory();
            ar.put(3949118105778132734L, _factory);
         }
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType(true) == 5;
      } else {
         return o instanceof NameSearchModel;
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
               NameSearchModel m = new NameSearchModel();
               String v = sb.getString(5, true);
               int nameType = sb.getInt(6, true);
               m.setValue(v, nameType);
               return m;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new NameSearchModel();
      }
   }
}

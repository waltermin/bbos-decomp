package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class TypeSearchModelFactory extends RIMModelFactory {
   private static final long REGKEY;
   private static TypeSearchModelFactory _factory;

   private TypeSearchModelFactory() {
   }

   public static final TypeSearchModelFactory getInstance() {
      if (_factory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _factory = (TypeSearchModelFactory)ar.getOrWaitFor(-4105314959393574545L);
         if (_factory == null) {
            _factory = new TypeSearchModelFactory();
            ar.put(-4105314959393574545L, _factory);
         }
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType() == 10;
      } else {
         return o instanceof TypeSearchModel;
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
               int v = sb.getInt(10, true);
               TypeSearchModel m = new TypeSearchModel();
               m._index = v;
               return m;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new TypeSearchModel();
      }
   }
}

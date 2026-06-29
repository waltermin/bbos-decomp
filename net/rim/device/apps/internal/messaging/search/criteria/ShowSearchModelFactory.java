package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class ShowSearchModelFactory extends RIMModelFactory {
   private static final long REGKEY = 7571377546306526038L;
   private static ShowSearchModelFactory _factory;

   private ShowSearchModelFactory() {
   }

   public static final ShowSearchModelFactory getInstance() {
      if (_factory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _factory = (ShowSearchModelFactory)ar.getOrWaitFor(7571377546306526038L);
         if (_factory == null) {
            _factory = new ShowSearchModelFactory();
            ar.put(7571377546306526038L, _factory);
         }
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType() == 7;
      } else {
         return o instanceof ShowSearchModel;
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
               int v = sb.getInt(7, true);
               ShowSearchModel m = new ShowSearchModel();
               m._index = v;
               return m;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new ShowSearchModel();
      }
   }
}

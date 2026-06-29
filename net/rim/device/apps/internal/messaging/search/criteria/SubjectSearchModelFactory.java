package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;

public final class SubjectSearchModelFactory extends RIMModelFactory {
   private static SubjectSearchModelFactory _factory = new SubjectSearchModelFactory();

   private SubjectSearchModelFactory() {
   }

   public static final SubjectSearchModelFactory getInstance() {
      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType(true) == 4;
      } else {
         return o instanceof SubjectSearchModel;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      RIMModel model = (RIMModel)ContextObject.get(context, 254);
      if (model != null) {
         TypeSearchModelFactory tf = TypeSearchModelFactory.getInstance();
         Object o = SubmemberUtilities.getFirstSubmember((ReadableList)model, tf);
         if (o != null) {
            RIMModel m = (RIMModel)o;
            TypeSearchModel tm = (TypeSearchModel)m;
            switch (tm._index) {
               case 1:
                  break;
               case 2:
               case 3:
               case 4:
               default:
                  return 0;
            }
         }
      }

      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return this.getMinimumCount(context);
   }

   @Override
   public final Object createInstance(Object context) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(context, 255);
         if (sb != null) {
            try {
               String v = sb.getString(4, true);
               SubjectSearchModel m = new SubjectSearchModel();
               m.setValue(v);
               return m;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new SubjectSearchModel();
      }
   }
}

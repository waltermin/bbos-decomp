package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class WebPageAddressModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object initialData) {
      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            initialData = syncBuffer.getString(54, true);
         } finally {
            return initialData != null ? new WebPageAddressModel(initialData) : null;
         }
      } else if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
         initialData = ((Vector)ContextObject.get(initialData, 249)).elementAt(1);
      }

      return initialData != null ? new WebPageAddressModel(initialData) : null;
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof WebPageAddressModel) {
         return true;
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         return syncBuffer != null && syncBuffer.getFieldType(true) == 54;
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         return data != null && data.size() > 0 && ((String)data.elementAt(0)).equals("URL");
      } else {
         return false;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }
}

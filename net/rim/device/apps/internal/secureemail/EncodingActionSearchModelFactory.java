package net.rim.device.apps.internal.secureemail;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class EncodingActionSearchModelFactory extends RIMModelFactory {
   @Override
   public boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(o, 255);
         return syncBuffer != null && syncBuffer.getFieldType(true) == 16;
      } else {
         return o instanceof EncodingActionSearchModel;
      }
   }

   @Override
   public Object createInstance(Object context) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(context, 255);
         if (syncBuffer != null) {
            try {
               int allowedEncodingActions = syncBuffer.getInt(16, true);
               EncodingActionSearchModel encodingActionSearchModel = new EncodingActionSearchModel();
               encodingActionSearchModel.setAllowedEncodingActions(allowedEncodingActions);
               return encodingActionSearchModel;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return new EncodingActionSearchModel();
      }
   }

   @Override
   public int getMaximumCount(Object context) {
      return 1;
   }
}

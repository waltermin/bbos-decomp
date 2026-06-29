package net.rim.device.apps.internal.implus;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;

final class IMPlusRequestOptionModelFactory extends RIMModelFactory {
   private byte _type;
   public static final byte DEFAULT_TYPE = 0;
   public static final byte DELIVERY_TYPE = 1;
   public static final byte READ_TYPE = 2;

   public IMPlusRequestOptionModelFactory(byte type) {
      this._type = type;
   }

   @Override
   public final Object createInstance(Object context) {
      Object returnVal = null;
      if (context instanceof ContextObject) {
         ContextObject ctx = (ContextObject)context;
         int serviceRecId = ContextObject.getIntegerData(context, -1);
         IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
         if (implusService != null) {
            int[] implusServiceRecIds = implusService.getReceiptCapableServiceRecIds();
            boolean found = false;

            for (int i = implusServiceRecIds.length - 1; i >= 0; i--) {
               if (implusServiceRecIds[i] == serviceRecId) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               return null;
            }

            if (this._type == 0) {
               return new IMPlusRequestOptionModelImpl(context);
            }

            ContextObject newCtx = ctx.clone();
            if (this._type == 1) {
               newCtx.setFlag(75);
            }

            returnVal = new IMPlusRequestOptionModelImpl(newCtx);
         }
      }

      return returnVal;
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof IMPlusRequestOptionModelImpl;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      return null;
   }
}

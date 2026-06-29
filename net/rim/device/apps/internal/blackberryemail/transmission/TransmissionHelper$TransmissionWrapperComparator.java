package net.rim.device.apps.internal.blackberryemail.transmission;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

final class TransmissionHelper$TransmissionWrapperComparator implements Comparator {
   private TransmissionHelper$TransmissionWrapperComparator() {
   }

   @Override
   public final int compare(Object object1, Object object2) {
      int retVal = 0;
      if (object1 instanceof EmailHeaderModel) {
         TransmissionWrapper wrapper1 = (EmailHeaderModel)object1;
         if (object2 instanceof EmailHeaderModel) {
            TransmissionWrapper wrapper2 = (EmailHeaderModel)object2;
            if (wrapper1.getScheduledActionTime() < wrapper2.getScheduledActionTime()) {
               return -1;
            }

            if (wrapper1.getScheduledActionTime() > wrapper2.getScheduledActionTime()) {
               retVal = 1;
            }
         }
      }

      return retVal;
   }

   @Override
   public final boolean equals(Object object) {
      return object instanceof TransmissionHelper$TransmissionWrapperComparator;
   }

   TransmissionHelper$TransmissionWrapperComparator(TransmissionHelper$1 x0) {
      this();
   }
}

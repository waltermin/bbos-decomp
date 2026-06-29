package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.vm.PersistentInteger;

class CFU {
   private static final int CFU_MODE_UNKNOWN;
   private static final int CFU_MODE_ON;
   private static final int CFU_MODE_OFF;
   private static final long CFU_MODE_GUID;
   private static int _id = PersistentInteger.getId(7592775292583405001L, 0);
   private static boolean _cffAvailable;

   static void setCFFAvailable(boolean available) {
      _cffAvailable = available;
   }

   static void update() {
      Phone phone = Phone.getInstance();
      int[] lineIds = PhoneUtilities.getAllLineIds();

      try {
         for (int i = lineIds.length - 1; i >= 0; i--) {
            if (phone.isCallForwardUnconditionalActive(lineIds[i])) {
               update(true);
               return;
            }
         }

         update(false);
      } finally {
         return;
      }
   }

   static void update(boolean active) {
      PersistentInteger.set(_id, active ? 1 : 2);
   }

   private static boolean doesRadioSayCFUIsActive(int line) {
      try {
         return Phone.getInstance().isCallForwardUnconditionalActive(line);
      } finally {
         ;
      }
   }

   static boolean isActive(int line) {
      if (PhoneUtilities.gsmTypeNetwork()) {
         if (!_cffAvailable && PhoneUtilities.getAllLineIds().length <= 1) {
            int mode = PersistentInteger.get(_id);
            return mode == 0 ? doesRadioSayCFUIsActive(line) : mode == 1;
         } else {
            return doesRadioSayCFUIsActive(line);
         }
      } else {
         return doesRadioSayCFUIsActive(line);
      }
   }
}

package net.rim.device.apps.api.transmission;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class EmailServiceBookRegistry {
   private ServiceRecord[] _array = new Object[0];
   private static final long ID;

   private EmailServiceBookRegistry() {
   }

   public static final EmailServiceBookRegistry getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         EmailServiceBookRegistry esbr = (EmailServiceBookRegistry)ar.get(-4682659685053076592L);
         if (null == esbr) {
            esbr = new EmailServiceBookRegistry();
            ar.put(-4682659685053076592L, esbr);
         }

         return esbr;
      }
   }

   public final void getEmailServiceRecords(ServiceRecord[] srs) {
      for (int i = 0; i < this._array.length; i++) {
         if (!Arrays.contains(srs, this._array[i])) {
            Arrays.add(srs, this._array[i]);
         }
      }
   }

   public final synchronized void register(ServiceRecord r) {
      if (!Arrays.contains(this._array, r)) {
         Arrays.add(this._array, r);
      }
   }
}

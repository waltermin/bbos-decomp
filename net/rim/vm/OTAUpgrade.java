package net.rim.vm;

import java.util.Vector;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Radio;
import net.rim.device.internal.system.RadioInternal;

public final class OTAUpgrade {
   public static final long DEFER_AUTO_OFF_GUID = 2108775066620843828L;
   private static final long OTASL_ONLY_COLLECTIONS_GUID = 3338648511322103566L;
   private static boolean _considerState = true;

   private OTAUpgrade() {
   }

   public static final native void setState(int var0);

   public static final native int getState();

   public static final native int getFailureCode();

   public static final native boolean isCapable();

   public static final synchronized boolean isRadioAllowedOn() {
      if (!_considerState) {
         return true;
      }

      int state = getState();
      switch (state) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 6:
         case 12:
         case 15:
            return true;
         default:
            return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final synchronized void requestRadioState(boolean on) {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         _considerState = false;
         if (on) {
            Radio.requestPowerOn();
            var3 = false;
         } else {
            Radio.requestPowerOff();
            var3 = false;
         }
      } finally {
         if (var3) {
            _considerState = true;
         }
      }

      _considerState = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final synchronized void activateRadios(int radios) {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         _considerState = false;
         RadioInternal.activateRadios(radios);
         var3 = false;
      } finally {
         if (var3) {
            _considerState = true;
         }
      }

      _considerState = true;
   }

   public static final void addOTASLOnlyCollection(SyncCollection sc) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         Object o = ar.get(3338648511322103566L);
         if (o == null || !(o instanceof Vector)) {
            o = new Vector();
            ar.put(3338648511322103566L, o);
         }

         Vector v = (Vector)o;
         v.addElement(sc);
      }
   }

   public static final SyncCollection[] getOTASLOnlyCollections() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Object o = ar.get(3338648511322103566L);
      if (!(o instanceof Vector)) {
         return new SyncCollection[0];
      }

      Vector v = (Vector)o;
      SyncCollection[] a = new SyncCollection[v.size()];
      v.copyInto(a);
      return a;
   }

   public static final boolean isOTASLInProgress() {
      int state = getState();
      switch (state) {
         case -1:
            return true;
         case 0:
         case 1:
         case 2:
         default:
            return false;
      }
   }
}

package net.rim.device.api.crypto.keystore;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntHashtable;

public final class KeyStoreSyncFields {
   private static final int EMAIL;
   private static final int DESKTOP_COOKIE;
   private static final int ISSUER;
   private static final int SERIAL_NUMBER;
   private static final int SMART_CARD_KEY;
   private static final int HISTORICAL_KEY_ID;
   private static final int PGP_KEY_ID;
   private static boolean _notMe = true;
   static final long HASHTABLE;

   private KeyStoreSyncFields() {
   }

   static final void initialize() {
      _notMe = false;
      register(3, -3741488786487467288L, true, true);
      register(2, -1124699153917633064L, true, true);
      register(15, 5689852616259641725L, false, true);
      register(17, 7970222113131699770L, false, true);
      register(28, -4699629744920546763L, true, false);
      register(30, 3198502480206239397L, false, true);
      register(31, 3622586747345475248L, false, true);
      _notMe = true;
   }

   public static final synchronized boolean register(int syncConstant, long associatedDataConstant, boolean up, boolean down) {
      IntHashtable hashtable = ApplicationRegistry.getApplicationRegistry().getIntHashtable(-6244618566307895102L);
      if (syncConstant < 100 && _notMe) {
         throw new Object();
      } else if (!hashtable.containsKey(syncConstant) && (up || down)) {
         hashtable.put(syncConstant, new Registration(syncConstant, associatedDataConstant, up, down));
         return true;
      } else {
         return false;
      }
   }
}

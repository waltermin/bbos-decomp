package net.rim.device.api.smartcard;

import net.rim.device.api.system.ApplicationRegistry;

public class CachedPasswordManager {
   protected static final long GUID;

   public static CachedPasswordManager getInstance() {
      return (CachedPasswordManager)ApplicationRegistry.getApplicationRegistry().waitForStartup(6437022998700452919L);
   }

   public void put(SmartCardID _1, String _2) {
      throw null;
   }

   public String get(SmartCardID _1) {
      throw null;
   }
}

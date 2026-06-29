package net.rim.device.apps.internal.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;

public class CryptoApplicationProperties {
   private long _flags;
   public static final long PGP_UNIVERSAL_SERVER_REGISTERED = 1L;
   private static final long ID = -8113189467387665237L;
   private static CryptoApplicationProperties _instance;

   public static CryptoApplicationProperties getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (CryptoApplicationProperties)applicationRegistry.getOrWaitFor(-8113189467387665237L);
         if (_instance == null) {
            _instance = new CryptoApplicationProperties();
            applicationRegistry.put(-8113189467387665237L, _instance);
         }
      }

      return _instance;
   }

   private CryptoApplicationProperties() {
   }

   public void setFlags(long flags) {
      this._flags |= flags;
   }

   public void clearFlags(long flags) {
      this._flags &= flags ^ -1;
   }

   public boolean testFlags(long flags) {
      return (this._flags & flags) == flags;
   }
}

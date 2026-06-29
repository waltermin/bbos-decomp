package net.rim.device.internal.i18n;

import net.rim.device.api.system.ApplicationRegistry;

public final class UnicodeServiceRegistry {
   private int _flags;
   private static final long REGISTRY_KEY;
   public static final int UNICODE_ENCODED;
   private static UnicodeServiceRegistry _instance;

   private UnicodeServiceRegistry() {
   }

   public static final UnicodeServiceRegistry getInstance() {
      return _instance;
   }

   public final int setFlags(int flags) {
      int f = this._flags;
      this._flags = flags;
      return f;
   }

   public final int getFlags() {
      return this._flags;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar != null) {
         _instance = (UnicodeServiceRegistry)ar.getOrWaitFor(-133996578404592302L);
         if (_instance == null) {
            _instance = new UnicodeServiceRegistry();
            ar.put(-133996578404592302L, _instance);
         }
      }
   }
}

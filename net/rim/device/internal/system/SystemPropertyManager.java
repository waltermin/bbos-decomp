package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class SystemPropertyManager {
   private SystemPropertyProvider[] _providers = new SystemPropertyProvider[0];
   private static final long GUID = -6763663770985155769L;

   private SystemPropertyManager() {
   }

   public static final SystemPropertyManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar == null) {
         return null;
      }

      SystemPropertyManager spm = (SystemPropertyManager)ar.getOrWaitFor(-6763663770985155769L);
      if (spm == null) {
         spm = new SystemPropertyManager();
         ar.put(-6763663770985155769L, spm);
      }

      return spm;
   }

   public final void addProvider(SystemPropertyProvider provider) {
      Arrays.add(this._providers, provider);
   }

   public final String getProperty(String property) {
      for (int i = this._providers.length - 1; i >= 0; i--) {
         String s = this._providers[i].getProperty(property);
         if (s != null) {
            return s;
         }
      }

      return null;
   }
}

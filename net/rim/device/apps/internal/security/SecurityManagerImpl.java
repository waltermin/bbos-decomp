package net.rim.device.apps.internal.security;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.SecurityManager;

final class SecurityManagerImpl implements SecurityManager {
   private ApplicationDescriptor _securityDescriptor;

   SecurityManagerImpl(ApplicationDescriptor securityDescriptor) {
      this._securityDescriptor = securityDescriptor;
      ((ApplicationManagerInternal)ApplicationManager.getApplicationManager()).setSecurityManager(this);
   }

   @Override
   public final boolean isLockRequired() {
      return Security.getInstance().isLockRequired();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final int lockSystem(boolean force) {
      boolean enabledPassword = Security.getInstance().isPasswordEnabled();
      if (FIPSPolicy.isDevicePasswordRequired() && !enabledPassword) {
         NvStore.writeInt(6, 1);
      }

      if (enabledPassword || force || ITPolicyInternal.policyHasChanged()) {
         try {
            Security.getInstance().incrementLockCounter();
            return ApplicationManager.getApplicationManager().runApplication(this._securityDescriptor);
         } catch (Throwable var5) {
            System.out.println(e.toString());
            return -1;
         }
      } else {
         return -1;
      }
   }
}

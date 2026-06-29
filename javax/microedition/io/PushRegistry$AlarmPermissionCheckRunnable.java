package javax.microedition.io;

import net.rim.device.internal.system.MIDletSecurity;

class PushRegistry$AlarmPermissionCheckRunnable implements Runnable {
   boolean _failed;
   boolean _done;
   RuntimeException _re;

   private PushRegistry$AlarmPermissionCheckRunnable() {
   }

   @Override
   public void run() {
      synchronized (this) {
         try {
            MIDletSecurity.checkPermission(8);
         } catch (Exception e) {
            this._failed = true;
            if (e instanceof RuntimeException) {
               this._re = (RuntimeException)e;
            }
         }

         this._done = true;
         throw new PushRegistry$PushRegistryPermissionCheckExitEvent(null);
      }
   }

   PushRegistry$AlarmPermissionCheckRunnable(PushRegistry$1 x0) {
      this();
   }
}

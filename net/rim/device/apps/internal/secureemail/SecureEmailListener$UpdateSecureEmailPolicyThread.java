package net.rim.device.apps.internal.secureemail;

import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;

class SecureEmailListener$UpdateSecureEmailPolicyThread extends Thread {
   private SecureEmailPolicyServer[] _serversRequiringUpdate;

   SecureEmailListener$UpdateSecureEmailPolicyThread(SecureEmailPolicyServer[] serversRequiringUpdate) {
      this._serversRequiringUpdate = serversRequiringUpdate;
   }

   @Override
   public void run() {
      int numServersRequiringUpdate = this._serversRequiringUpdate.length;

      for (int i = 0; i < numServersRequiringUpdate; i++) {
         try {
            this._serversRequiringUpdate[i].updatePolicy(null);
         } finally {
            continue;
         }
      }
   }
}

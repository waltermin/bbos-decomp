package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SecureEmailAwareSendMethod$UpdateSecureEmailPolicyWorkerThread extends PleaseWaitWorkerThread {
   private SecureEmailPolicyServer[] _secureEmailPolicyServers;

   public SecureEmailAwareSendMethod$UpdateSecureEmailPolicyWorkerThread(SecureEmailPolicyServer[] secureEmailPolicyServers) {
      this._secureEmailPolicyServers = secureEmailPolicyServers;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void doWork() {
      AbortSendSecureEmailException e;
      try {
         try {
            SecureEmailServerOperationListener secureEmailServerOperationListener = (SecureEmailServerOperationListener)super._pleaseWaitDialog;
            int numSecureEmailPolicyServers = this._secureEmailPolicyServers.length;

            for (int i = 0; i < numSecureEmailPolicyServers; i++) {
               SecureEmailPolicyServer currentPolicyServer = this._secureEmailPolicyServers[i];
               if (!currentPolicyServer.initialize(secureEmailServerOperationListener)) {
                  throw new AbortSendSecureEmailException();
               }

               if (currentPolicyServer.isPolicyUpdateRequired() && !currentPolicyServer.updatePolicy(secureEmailServerOperationListener)) {
                  throw new AbortSendSecureEmailException();
               }
            }

            return;
         } catch (AbortSendSecureEmailException var7) {
            e = var7;
         }
      } catch (Throwable var8) {
         this.setThrowable(e);
         return;
      }

      this.setThrowable(e);
   }
}

package net.rim.device.apps.internal.iota;

import net.rim.device.apps.api.iota.IOTAManager;

final class IOTAManagerImpl extends IOTAManager {
   private ProvisioningServiceAgent _agent;

   public IOTAManagerImpl(ProvisioningServiceAgent provisioningServiceAgent) {
      this._agent = provisioningServiceAgent;
   }

   static final void register(ProvisioningServiceAgent provisioningServiceAgent) {
      IOTAManager.register(new IOTAManagerImpl(provisioningServiceAgent));
   }

   @Override
   public final boolean initiateIOTA(int mode) {
      return this._agent.initiateSession(mode, null);
   }

   @Override
   public final boolean cancelIOTA() {
      return this._agent.cancelIOTASession();
   }

   @Override
   public final int currentStatus() {
      return this._agent.currentStatus();
   }
}

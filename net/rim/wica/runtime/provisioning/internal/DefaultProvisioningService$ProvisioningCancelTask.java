package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningTaskInfo;

final class DefaultProvisioningService$ProvisioningCancelTask implements ProvisioningTaskInfo {
   private String _uri;

   public final DeploymentDescriptor getDD() {
      return null;
   }

   @Override
   public final String getApplicationUri() {
      return this._uri;
   }

   @Override
   public final long getApplicationId() {
      return -1;
   }

   DefaultProvisioningService$ProvisioningCancelTask(String uri) {
      this._uri = uri;
   }
}

package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;

public final class UpgradeTaskInfo implements Persistable {
   private DeploymentDescriptor _deploymentDescriptor;
   private int _action;
   private long _expiryDate;
   private boolean _shownDialog;

   public UpgradeTaskInfo() {
   }

   public UpgradeTaskInfo(DeploymentDescriptor deploymentDescriptor, long expiryDate) {
      this._deploymentDescriptor = deploymentDescriptor;
      this._expiryDate = expiryDate;
   }

   public UpgradeTaskInfo(DeploymentDescriptor deploymentDescriptor, int action, String applicationURL, long applicationID) {
      this._deploymentDescriptor = deploymentDescriptor;
      this._action = action;
   }

   public final DeploymentDescriptor getDeploymentDescriptor() {
      return this._deploymentDescriptor;
   }

   public final int getAction() {
      return this._action;
   }

   public final long getExpiryDate() {
      return this._expiryDate;
   }

   public final boolean isExpired() {
      long expiry = this.getExpiryDate();
      return expiry == 0 || expiry < System.currentTimeMillis();
   }

   public final boolean getShownDialog() {
      return this._shownDialog;
   }

   public final void setShownDialog(boolean shownDialog) {
      this._shownDialog = shownDialog;
   }

   public final void setAction(int action) {
      this._action = action;
   }

   public final void setDeploymentDescriptor(DeploymentDescriptor descriptor) {
      this._deploymentDescriptor = descriptor;
   }

   public final void setExpiryDate(long date) {
      this._expiryDate = date;
   }
}

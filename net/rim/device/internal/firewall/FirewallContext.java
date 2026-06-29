package net.rim.device.internal.firewall;

import net.rim.device.api.system.ApplicationDescriptor;

public class FirewallContext {
   private ApplicationDescriptor _requestingDescriptor;
   private int[] _additionalModules;

   public void setRequestingDescriptor(ApplicationDescriptor requestingDescriptor) {
      this._requestingDescriptor = requestingDescriptor;
   }

   public ApplicationDescriptor getRequestingDescriptor() {
      return this._requestingDescriptor;
   }

   public void setAdditionalModules(int[] additionalModules) {
      this._additionalModules = additionalModules;
   }

   public int[] getAdditionalModules() {
      return this._additionalModules;
   }
}

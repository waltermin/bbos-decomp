package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.util.Arrays;

class ServiceProgramUI$DependentInfo {
   int[] _dependedBy = new int[0];
   int _dependsOn;

   protected ServiceProgramUI$DependentInfo(int dependsOn) {
      this._dependsOn = dependsOn;
   }

   protected void addDependedBy(int dependedBy) {
      Arrays.add(this._dependedBy, dependedBy);
   }
}

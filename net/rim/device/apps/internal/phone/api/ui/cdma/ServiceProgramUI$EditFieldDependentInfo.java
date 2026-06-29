package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.util.Arrays;

final class ServiceProgramUI$EditFieldDependentInfo extends ServiceProgramUI$DependentInfo {
   int[] _delimitter = new int[0];

   public ServiceProgramUI$EditFieldDependentInfo(int dependsOn, int dependedBy, char delimitter) {
      super(dependsOn);
      this.add(dependedBy, delimitter);
   }

   public final void add(int dependedBy, char delimitter) {
      super.addDependedBy(dependedBy);
      Arrays.add(this._delimitter, delimitter);
   }
}

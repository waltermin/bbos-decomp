package net.rim.device.apps.internal.phone.api.ui.cdma;

final class ServiceProgramUI$ChoiceFieldDependentInfo extends ServiceProgramUI$DependentInfo {
   public ServiceProgramUI$ChoiceFieldDependentInfo(int dependsOn, int dependedBy) {
      super(dependsOn);
      this.add(dependedBy);
   }

   public final void add(int dependedBy) {
      super.addDependedBy(dependedBy);
   }
}

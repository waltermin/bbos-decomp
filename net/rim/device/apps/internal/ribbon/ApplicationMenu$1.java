package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.ui.component.ButtonField;

final class ApplicationMenu$1 extends ButtonField {
   private final ApplicationMenu this$0;

   ApplicationMenu$1(ApplicationMenu _1, String x0, long x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.this$0.onOrganizeApplications();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return key == '\n' ? this.this$0.onOrganizeApplications() : super.keyChar(key, status, time);
   }
}

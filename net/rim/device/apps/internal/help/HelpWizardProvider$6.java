package net.rim.device.apps.internal.help;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class HelpWizardProvider$6 extends MenuItem {
   private final HelpWizardProvider this$0;

   HelpWizardProvider$6(HelpWizardProvider _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.doBack();
   }
}

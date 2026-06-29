package net.rim.device.apps.internal.help;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;

final class HelpWizardProvider$1 extends VerticalFieldManager {
   private final HelpWizardProvider this$0;

   HelpWizardProvider$1(HelpWizardProvider _1, long x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   protected final void paint(Graphics graphics) {
      HelpWizardProvider.paintBackgroundColor(this.getExtent(), graphics, 16777215);
      super.paint(graphics);
   }
}

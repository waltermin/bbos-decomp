package net.rim.device.internal.system;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.container.DialogFieldManager;

final class SIMCardEfHandler$PleaseWaitDialog extends Screen {
   SIMCardEfHandler$PleaseWaitDialog() {
      super(new DialogFieldManager(), 68719476736L);
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(0, 0);
   }
}

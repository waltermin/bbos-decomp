package net.rim.device.internal.ui;

import net.rim.device.api.ui.UiEngine;

class ApplicationSwitcher$ResumePainting implements Runnable {
   UiEngine _engineToSuspend;

   ApplicationSwitcher$ResumePainting(UiEngine engineToSuspend) {
      this._engineToSuspend = engineToSuspend;
   }

   @Override
   public void run() {
      this._engineToSuspend.suspendPainting(false);
      this._engineToSuspend.repaint();
   }
}

package net.rim.device.api.ui.container;

import net.rim.device.api.ui.UiApplication;

class Tooltip$TooltipInvoker implements Runnable {
   Tooltip _tooltip;

   Tooltip$TooltipInvoker(Tooltip tooltip) {
      this._tooltip = tooltip;
   }

   @Override
   public void run() {
      this._tooltip._screen.setTooltip(this._tooltip);
      UiApplication.getUiApplication().pushScreen(this._tooltip._screen);
      this._tooltip._popScreenRunnable.cancelInvokeLater();
      this._tooltip._popScreenRunnable.init();
      this._tooltip._popScreenRunnable._invokeId = UiApplication.getUiApplication()
         .invokeLater(this._tooltip._popScreenRunnable, this._tooltip._duration, false);
   }
}

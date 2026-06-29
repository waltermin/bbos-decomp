package net.rim.device.apps.internal.lbs.protocol;

import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class HelpDialog extends PopupScreen {
   private boolean _connScreenLaunched = false;

   HelpDialog() {
      super(new VerticalFieldManager());
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached && !this._connScreenLaunched) {
         label21:
         try {
            RibbonLauncher.getInstance().launch("net_rim_bb_manage_connections");
            this._connScreenLaunched = true;
         } finally {
            break label21;
         }
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final void onExposed() {
      if (this._connScreenLaunched) {
         this.close();
      }

      super.onExposed();
   }
}

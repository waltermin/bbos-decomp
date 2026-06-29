package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.ui.component.Dialog;

final class PMEBrowserField$1 implements Runnable {
   private final PMEBrowserField this$0;

   PMEBrowserField$1(PMEBrowserField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Dialog.alert(this.this$0._popmsg);
   }
}

package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.BrowserScreen;

final class TabField extends LabelField {
   private int _index;

   public TabField(String label, int index) {
      super(label, 18014398509482048L);
      this._index = index;
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      BrowserScreen screen = BrowserDaemonRegistry.getInstance().getBrowserScreen();
      screen.setActiveTab(this._index);
   }
}

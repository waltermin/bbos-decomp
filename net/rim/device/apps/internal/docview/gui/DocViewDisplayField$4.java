package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.ui.InputMethodSwitcher;
import net.rim.tid.util.Utils;

class DocViewDisplayField$4 extends MenuItem {
   private final DocViewDisplayField this$0;

   DocViewDisplayField$4(DocViewDisplayField _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Locale[] locales = Utils.getAvailableInputLocales(true);
      new InputMethodSwitcher(locales, Utils.getInputLocalesDisplayNames(locales), true, null);
   }
}

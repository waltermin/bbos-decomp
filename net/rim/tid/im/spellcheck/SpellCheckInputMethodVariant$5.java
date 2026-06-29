package net.rim.tid.im.spellcheck;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class SpellCheckInputMethodVariant$5 extends MenuItem {
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$5(SpellCheckInputMethodVariant _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.controlObjectActionPerformed(47, null);
   }
}

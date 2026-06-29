package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.ListField;

final class ThemeOptionsScreen$1 extends ListField {
   private final ThemeOptionsScreen this$0;

   ThemeOptionsScreen$1(ThemeOptionsScreen _1) {
      this.this$0 = _1;
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int result = super.moveFocus(amount, status, time);
      this.this$0.listFocusChanged(this.getSelectedIndex());
      return result;
   }
}

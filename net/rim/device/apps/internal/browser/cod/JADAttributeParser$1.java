package net.rim.device.apps.internal.browser.cod;

import net.rim.device.api.ui.text.IPTextFilter;

class JADAttributeParser$1 extends IPTextFilter {
   private final JADAttributeParser this$0;

   JADAttributeParser$1(JADAttributeParser _1, int x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public boolean validate(char c) {
      return c != '*' && c != '?' ? super.validate(c) : true;
   }
}

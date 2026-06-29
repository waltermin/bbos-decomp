package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Field;

class DateField$2 extends DateField {
   private final DateField this$0;

   DateField$2(DateField _1, String x0, long x1, DateFormat x2, long x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   protected void onFocus(int direction) {
      boolean firstFocus = false;
      if (super._first_focus) {
         firstFocus = true;
      }

      super.onFocus(direction);
      if (firstFocus) {
         DateField outer = this.this$0;
         super._position = outer._position;
         super.calcCachedData();
      }
   }

   @Override
   public Field getOriginal() {
      return this.this$0;
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status | 1, time);
   }
}

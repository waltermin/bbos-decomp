package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;

class DurationField$2 extends DurationField {
   private final DurationField this$0;

   DurationField$2(DurationField _1, String x0, int x1, int x2, long x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public Field getOriginal() {
      return this.this$0;
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status | 1, time);
   }

   @Override
   int getFieldText_Y() {
      return 0;
   }
}

package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.Field;

class BaseDurationField$1 extends BaseDurationField {
   private final BaseDurationField this$0;

   BaseDurationField$1(BaseDurationField this$0, int x0, int x1, long x2, long x3) {
      super(x0, x1, x2, x3);
      this.this$0 = this$0;
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

package net.rim.device.api.ui;

class FieldForeignObject$2 implements Runnable {
   private final FieldForeignObject this$0;

   FieldForeignObject$2(FieldForeignObject _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      MediaField.setFocus(this.this$0._field, 1);
   }
}

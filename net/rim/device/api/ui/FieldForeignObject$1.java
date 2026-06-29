package net.rim.device.api.ui;

class FieldForeignObject$1 implements Runnable {
   private final FieldForeignObject this$0;

   FieldForeignObject$1(FieldForeignObject _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._field.updateLayout();
   }
}

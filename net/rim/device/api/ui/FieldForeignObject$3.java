package net.rim.device.api.ui;

class FieldForeignObject$3 implements Runnable {
   private final FieldForeignObject this$0;

   FieldForeignObject$3(FieldForeignObject _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      MediaField m = (MediaField)this.this$0._field.getManager();
      if (m != null) {
         m.killFocus(this.this$0._field);
      }
   }
}

package net.rim.device.apps.internal.qm.peer;

final class ContactTreeField$1 implements Runnable {
   private final Object val$object;
   private final ContactTreeField this$0;

   ContactTreeField$1(ContactTreeField _1, Object _2) {
      this.this$0 = _1;
      this.val$object = _2;
   }

   @Override
   public final void run() {
      ContactTreeField.access$200(this.this$0, this.val$object);
   }
}

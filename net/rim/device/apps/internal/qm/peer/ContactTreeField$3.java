package net.rim.device.apps.internal.qm.peer;

final class ContactTreeField$3 implements Runnable {
   private final Object val$element;
   private final ContactTreeField this$0;

   ContactTreeField$3(ContactTreeField _1, Object _2) {
      this.this$0 = _1;
      this.val$element = _2;
   }

   @Override
   public final void run() {
      ContactTreeField.access$400(this.this$0, this.val$element);
   }
}

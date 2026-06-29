package net.rim.device.apps.internal.qm.peer;

final class ContactTreeField$2 implements Runnable {
   private final Object val$element;
   private final ContactTreeField this$0;

   ContactTreeField$2(ContactTreeField _1, Object _2) {
      this.this$0 = _1;
      this.val$element = _2;
   }

   @Override
   public final void run() {
      ContactTreeField.access$300(this.this$0, this.val$element);
   }
}

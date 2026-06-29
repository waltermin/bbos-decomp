package net.rim.device.apps.internal.qm.peer;

final class ContactTreeField$4 implements Runnable {
   private final Object val$oldElement;
   private final Object val$newElement;
   private final ContactTreeField this$0;

   ContactTreeField$4(ContactTreeField _1, Object _2, Object _3) {
      this.this$0 = _1;
      this.val$oldElement = _2;
      this.val$newElement = _3;
   }

   @Override
   public final void run() {
      ContactTreeField.access$500(this.this$0, this.val$oldElement, this.val$newElement);
   }
}

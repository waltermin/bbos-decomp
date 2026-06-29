package net.rim.device.apps.internal.phone;

final class StandardCall$1 implements Runnable {
   private final StandardCall this$0;

   StandardCall$1(StandardCall _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._afterDialToneHandler.start(true);
   }
}

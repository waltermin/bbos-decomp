package net.rim.device.apps.internal.blackberryemail.email;

class NativeAttachmentViewer$2 extends Thread {
   private final Runnable val$runnable;
   private final NativeAttachmentViewer this$0;

   NativeAttachmentViewer$2(NativeAttachmentViewer _1, Runnable _2) {
      this.this$0 = _1;
      this.val$runnable = _2;
   }

   @Override
   public void run() {
      this.val$runnable.run();
   }
}

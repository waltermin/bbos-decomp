package net.rim.device.apps.internal.blackberryemail.email;

class NativeAttachmentViewer$1 implements Runnable {
   private final NativeAttachmentViewer this$0;

   NativeAttachmentViewer$1(NativeAttachmentViewer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._screen.close();
   }
}

package net.rim.device.apps.internal.blackberryemail.email;

class NativeAttachmentViewer$3$1 implements Runnable {
   private final NativeAttachmentViewer$3 this$1;

   NativeAttachmentViewer$3$1(NativeAttachmentViewer$3 _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.this$0._screen.getDelegate().replace(this.this$1.val$contentField, this.this$1.this$0.createRenderErrorField());
   }
}

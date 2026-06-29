package net.rim.device.apps.internal.docview.gui;

class AttachmentViewerFactory$ParseThread$1 extends Thread {
   private final AttachmentViewerFactory$ParseThread this$0;

   AttachmentViewerFactory$ParseThread$1(AttachmentViewerFactory$ParseThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._progressThread.doStart();
   }
}

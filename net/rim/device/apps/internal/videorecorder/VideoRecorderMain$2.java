package net.rim.device.apps.internal.videorecorder;

final class VideoRecorderMain$2 implements Runnable {
   private final VideoRecorderMain this$0;

   VideoRecorderMain$2(VideoRecorderMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._mediaDialog != null) {
         this.this$0.pushScreen(this.this$0._mediaDialog);
      }
   }
}

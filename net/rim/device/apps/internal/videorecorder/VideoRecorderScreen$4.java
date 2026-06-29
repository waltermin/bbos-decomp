package net.rim.device.apps.internal.videorecorder;

final class VideoRecorderScreen$4 implements Runnable {
   private final VideoRecorderScreen this$0;

   VideoRecorderScreen$4(VideoRecorderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.activateStop0();
   }
}

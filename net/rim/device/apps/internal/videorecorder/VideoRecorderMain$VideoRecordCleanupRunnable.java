package net.rim.device.apps.internal.videorecorder;

final class VideoRecorderMain$VideoRecordCleanupRunnable implements Runnable {
   private final VideoRecorderMain this$0;

   private VideoRecorderMain$VideoRecordCleanupRunnable(VideoRecorderMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.cleanup();
   }

   VideoRecorderMain$VideoRecordCleanupRunnable(VideoRecorderMain x0, VideoRecorderMain$1 x1) {
      this(x0);
   }
}

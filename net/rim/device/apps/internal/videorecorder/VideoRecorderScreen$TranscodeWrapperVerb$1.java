package net.rim.device.apps.internal.videorecorder;

final class VideoRecorderScreen$TranscodeWrapperVerb$1 implements Runnable {
   private final VideoRecorderScreen$TranscodeWrapperVerb this$1;

   VideoRecorderScreen$TranscodeWrapperVerb$1(VideoRecorderScreen$TranscodeWrapperVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1._wrappedVerb.invoke(this.this$1._invokeParameter);
   }
}

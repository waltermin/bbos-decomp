package net.rim.device.apps.internal.voicenotesrecorder;

final class RecordVoiceNoteScreen$1 implements Runnable {
   private final RecordVoiceNoteScreen this$0;

   RecordVoiceNoteScreen$1(RecordVoiceNoteScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.activatePause();
      this.this$0._autoPause = true;
   }
}

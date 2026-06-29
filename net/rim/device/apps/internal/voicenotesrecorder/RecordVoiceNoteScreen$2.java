package net.rim.device.apps.internal.voicenotesrecorder;

final class RecordVoiceNoteScreen$2 implements Runnable {
   private final RecordVoiceNoteScreen this$0;

   RecordVoiceNoteScreen$2(RecordVoiceNoteScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.activateResume();
      this.this$0._autoPause = false;
   }
}

package net.rim.device.apps.internal.qm.peer;

final class VoiceNoteDialog$TimerUpdateRunnable implements Runnable {
   private final VoiceNoteDialog this$0;

   VoiceNoteDialog$TimerUpdateRunnable(VoiceNoteDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int percent = 0;
      if (this.this$0.isPlaying()) {
         percent = (int)((System.currentTimeMillis() - VoiceNoteDialog.access$1000(this.this$0) + 500) * 100 / VoiceNoteDialog.access$1100(this.this$0));
      } else {
         int time = (int)((VoiceNoteDialog.access$1100(this.this$0) + System.currentTimeMillis() - VoiceNoteDialog.access$1000(this.this$0) + 500) / 1000);
         percent = VoiceNoteDialog.access$500(this.this$0).getPercentFull(time);
      }

      VoiceNoteDialog.access$100(this.this$0).setText(((StringBuffer)(new Object())).append(Integer.toString(percent)).append(" %").toString());
   }
}

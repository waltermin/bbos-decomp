package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.resource.QmResources;

final class VoiceNoteDialog$1 implements Runnable {
   private final VoiceNoteDialog this$0;

   VoiceNoteDialog$1(VoiceNoteDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0.isPlaying()) {
         VoiceNoteDialog.access$000(this.this$0).setText(QmResources.getString(17));
      } else {
         VoiceNoteDialog.access$000(this.this$0).setText(QmResources.getString(106));
      }

      VoiceNoteDialog.access$100(this.this$0).setText("0 %");
      VoiceNoteDialog.access$200(this.this$0).deleteAll();
      VoiceNoteDialog.access$200(this.this$0).add(VoiceNoteDialog.access$300(this.this$0));
      VoiceNoteDialog.access$400(this.this$0);
   }
}

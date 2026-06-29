package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.resource.QmResources;

final class VoiceNoteDialog$2 implements Runnable {
   private final VoiceNoteDialog this$0;

   VoiceNoteDialog$2(VoiceNoteDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.stop();
      VoiceNoteDialog.access$000(this.this$0).setText(QmResources.getString(103));
      int size = VoiceNoteDialog.access$500(this.this$0).size();
      VoiceNoteDialog.access$100(this.this$0)
         .setText(((StringBuffer)(new Object())).append(Integer.toString(size)).append(" ").append(QmResources.getString(104)).toString());
      VoiceNoteDialog.access$200(this.this$0).deleteAll();
      VoiceNoteDialog.access$200(this.this$0).add(VoiceNoteDialog.access$600(this.this$0));
      VoiceNoteDialog.access$200(this.this$0).add(VoiceNoteDialog.access$700(this.this$0));
      VoiceNoteDialog.access$200(this.this$0).add(VoiceNoteDialog.access$800(this.this$0));
      VoiceNoteDialog.access$700(this.this$0).setFocus();
      VoiceNoteDialog.access$900(this.this$0);
   }
}

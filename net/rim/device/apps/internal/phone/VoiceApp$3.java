package net.rim.device.apps.internal.phone;

import java.util.Vector;

final class VoiceApp$3 implements Runnable {
   private final Vector val$calls;
   private final VoiceApp this$0;

   VoiceApp$3(VoiceApp _1, Vector _2) {
      this.this$0 = _1;
      this.val$calls = _2;
   }

   @Override
   public final void run() {
      this.this$0._activeScreen.updateCalls(this.val$calls);
      if (this.this$0._editNotesScreen != null && this.this$0._editNotesScreen.isDisplayed()) {
         this.this$0._editNotesScreen.updateCalls(this.val$calls);
      }
   }
}

package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.api.PhoneLogger;

final class VoiceApp$4 implements Runnable {
   private final VoiceApp this$0;

   VoiceApp$4(VoiceApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!this.this$0.isForeground()) {
         this.this$0.popActiveScreen();
      } else if (this.this$0._active) {
         PhoneLogger.log("switchbgoi-now-actv-return");
      } else {
         if (this.this$0._gotCallsEmpty) {
            this.this$0._activeScreen.updateCalls(null);
         }

         this.this$0._switchBackgroundOnIdle = false;
         if (this.this$0._switchBackgroundConfirmation == null) {
            this.this$0._switchedBackgroundOnIdle = true;
            PhoneLogger.log("noconf-rqstBG");
            this.this$0.requestBackground();
         } else {
            if (this.this$0._switchBackgroundConfirmation.confirm(null, null)) {
               this.this$0._switchedBackgroundOnIdle = true;
               PhoneLogger.log("conf-rqstBG");
               this.this$0.requestBackground();
            } else {
               PhoneLogger.log("conf-failed-popactvscrn");
               this.this$0.popActiveScreen();
            }

            this.this$0._switchBackgroundConfirmation = null;
         }
      }
   }
}

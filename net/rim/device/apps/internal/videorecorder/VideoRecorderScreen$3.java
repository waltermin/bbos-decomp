package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapperSelectionDialog;

final class VideoRecorderScreen$3 implements Runnable {
   private final VideoRecorderScreen this$0;

   VideoRecorderScreen$3(VideoRecorderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.runTerminalVerbIfApplicable();
      this.this$0._sendVerbs = MIMEContentVerbRepository.getVerbs("video/3gpp");
      if (this.this$0._sendVerbs != null && this.this$0._sendVerbs.length > 0) {
         int idx = 0;
         if (this.this$0._sendVerbs.length > 1) {
            String[] verbStrings = new String[this.this$0._sendVerbs.length];
            int i = this.this$0._sendVerbs.length;

            while (--i >= 0) {
               verbStrings[i] = this.this$0._sendVerbs[i].toString();
            }

            Dialog pickVerb = new PopupVerbWrapperSelectionDialog(VideoRecorderResources.getString(20), verbStrings, 0, true);
            pickVerb.setEscapeEnabled(true);
            pickVerb.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
            idx = pickVerb.doModal();
            if (idx == -1) {
               return;
            }
         }

         if (this.this$0._sendVerbs[idx] != null) {
            this.this$0._sendVerbs[idx].invoke(this.this$0.createContextForSendVerb());
         }
      }
   }
}

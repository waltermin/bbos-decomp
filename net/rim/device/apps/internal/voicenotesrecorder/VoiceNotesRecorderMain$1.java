package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.voicenotesrecorder.resource.RecordVoiceNotesResources;

final class VoiceNotesRecorderMain$1 implements Runnable {
   private final VoiceNotesRecorderMain this$0;

   VoiceNotesRecorderMain$1(VoiceNotesRecorderMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Dialog d = (Dialog)(new Object(0, RecordVoiceNotesResources.getString(14), 0, null, 0));
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      this.this$0.pushGlobalScreen(d, 50, 1);
      System.exit(0);
   }
}

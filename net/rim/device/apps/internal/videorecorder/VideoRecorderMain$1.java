package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

final class VideoRecorderMain$1 implements Runnable {
   private final VideoRecorderMain this$0;

   VideoRecorderMain$1(VideoRecorderMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Dialog d = (Dialog)(new Object(0, VideoRecorderResources.getString(3), 0, null, 0));
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      this.this$0.pushGlobalScreen(d, 50, 1);
      System.exit(0);
   }
}

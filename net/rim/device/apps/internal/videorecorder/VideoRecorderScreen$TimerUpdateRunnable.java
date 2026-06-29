package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.system.Backlight;
import net.rim.device.internal.system.InternalServices;

final class VideoRecorderScreen$TimerUpdateRunnable implements Runnable {
   private final VideoRecorderScreen this$0;

   VideoRecorderScreen$TimerUpdateRunnable(VideoRecorderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int time = (int)(this.this$0._accumulatedTime + InternalServices.getUptime() - this.this$0._recordStartTime);
      this.this$0.updateElapsedTime(this.this$0.formatTime(time));
      this.this$0.updateMemoryRemainingIndicator(this.this$0._vrc.getPercentMemoryRemaining());
      Backlight.resetElapsedTime();
   }
}

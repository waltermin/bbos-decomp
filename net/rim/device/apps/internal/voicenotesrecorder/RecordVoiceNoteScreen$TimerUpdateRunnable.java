package net.rim.device.apps.internal.voicenotesrecorder;

import net.rim.device.internal.system.InternalServices;

final class RecordVoiceNoteScreen$TimerUpdateRunnable implements Runnable {
   private final RecordVoiceNoteScreen this$0;

   RecordVoiceNoteScreen$TimerUpdateRunnable(RecordVoiceNoteScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int time = (int)(this.this$0._accumulatedTime + InternalServices.getUptime() - this.this$0._recordStartTime);
      this.this$0.updateElapsedTime(this.this$0.formatTime(time + 500));
      int percentLeft = (int)(this.this$0._maxTime - time);
      percentLeft *= 100;
      if (this.this$0._maxTime > 0) {
         percentLeft = (int)(percentLeft / this.this$0._maxTime);
         percentLeft += 5;
      } else {
         percentLeft = 0;
      }

      this.this$0.updateMemoryRemainingIndicator(percentLeft);
      if (this.this$0._maxTime > 0) {
         if (time >= this.this$0._maxTime) {
            this.this$0.activateStop(false);
            this.this$0.updateElapsedTime(this.this$0.formatTime(0));
            if (this.this$0.enoughSpaceAvail(this.this$0.getTimeAvailable())) {
               this.this$0.activateRecord();
            }
         }

         if (this.this$0.isRecording() && this.this$0._imageField.isAnimated() && !this.this$0._imageField.isAnimationRunning()) {
            this.this$0._imageField.startAnimation();
         }
      }
   }
}

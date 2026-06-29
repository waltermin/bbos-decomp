package net.rim.device.internal.media;

class MidiPlayer$MediaTimeEvent implements Runnable {
   private final MidiPlayer this$0;

   MidiPlayer$MediaTimeEvent(MidiPlayer _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (this.this$0) {
         boolean cancel = false;

         try {
            long duration = this.this$0.getDuration();
            this.this$0._mediaTime += 1000000;
            if (this.this$0._mediaTime > 0 && this.this$0._mediaTime <= duration) {
               this.this$0.notifyListeners("com.rim.timeUpdate", new Long(this.this$0._mediaTime));
            } else {
               cancel = true;
            }
         } finally {
            ;
         }

         if (cancel && this.this$0._updateTimeEventId != -1) {
            this.this$0.getApplication().cancelInvokeLater(this.this$0._updateTimeEventId);
            this.this$0._updateTimeEventId = -1;
         }
      }
   }
}

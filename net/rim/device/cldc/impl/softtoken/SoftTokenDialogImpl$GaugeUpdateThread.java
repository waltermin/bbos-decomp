package net.rim.device.cldc.impl.softtoken;

public final class SoftTokenDialogImpl$GaugeUpdateThread extends Thread {
   public boolean _run;
   private final SoftTokenDialogImpl this$0;
   private static final int MAX_SECONDS = 60;
   private static final int SEC_DIVISOR = 6;
   public static final int MAX_GAUGE_VALUE = 9;

   public SoftTokenDialogImpl$GaugeUpdateThread(SoftTokenDialogImpl _1) {
      this.this$0 = _1;
      this._run = true;
   }

   @Override
   public final void run() {
      this.this$0.updateCurrentCode();

      while (this._run) {
         int seconds = this.adjustGauge();
         seconds %= 6;
         if (seconds == 0) {
            seconds = 1;
         }

         synchronized (this) {
            try {
               this.wait(seconds * 1000);
            } finally {
               continue;
            }
         }
      }

      this.this$0._gaugeUpdateThread = null;
   }

   private final int adjustGauge() {
      int secondsRemaining = 60;

      label35:
      try {
         secondsRemaining = this.this$0._rimSecurIDLib.getTokenSecondsRemaining(this.this$0._softToken._tag);
      } finally {
         break label35;
      }

      if (secondsRemaining > 50 && !this.this$0.updateCurrentCode()) {
         return secondsRemaining;
      }

      int value = secondsRemaining / 6;
      if (value >= 9) {
         value = 9;
      }

      this.this$0._gaugeField.setValue(value);
      return secondsRemaining;
   }
}

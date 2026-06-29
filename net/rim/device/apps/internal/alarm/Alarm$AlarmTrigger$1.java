package net.rim.device.apps.internal.alarm;

final class Alarm$AlarmTrigger$1 implements Runnable {
   private final Alarm$AlarmTrigger this$0;

   Alarm$AlarmTrigger$1(Alarm$AlarmTrigger _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._dialog.select(AlarmOptions.getOptions().getSnooze() == 0 ? 0 : 1);
   }
}

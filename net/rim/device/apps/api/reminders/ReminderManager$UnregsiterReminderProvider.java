package net.rim.device.apps.api.reminders;

import net.rim.device.api.util.Arrays;

final class ReminderManager$UnregsiterReminderProvider implements Runnable {
   ReminderProvider _rmProvider;
   private final ReminderManager this$0;

   ReminderManager$UnregsiterReminderProvider(ReminderManager _1, ReminderProvider rmProvider) {
      this.this$0 = _1;
      this._rmProvider = rmProvider;
   }

   @Override
   public final void run() {
      synchronized (this.this$0._reminderProviders) {
         for (int i = this.this$0._reminderProviders.length - 1; i >= 0; i--) {
            if (this.this$0._reminderProviders[i] == this._rmProvider) {
               Arrays.removeAt(this.this$0._reminderProviders, i);
               Arrays.removeAt(this.this$0._reminders, i);
               Arrays.removeAt(this.this$0._reminderProviderEnabled, i);
               break;
            }
         }
      }
   }
}

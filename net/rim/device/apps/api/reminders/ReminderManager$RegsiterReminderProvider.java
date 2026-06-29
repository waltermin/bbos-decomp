package net.rim.device.apps.api.reminders;

import net.rim.vm.Array;

final class ReminderManager$RegsiterReminderProvider implements Runnable {
   ReminderProvider _rmProvider;
   private final ReminderManager this$0;

   ReminderManager$RegsiterReminderProvider(ReminderManager _1, ReminderProvider rmProvider) {
      this.this$0 = _1;
      this._rmProvider = rmProvider;
   }

   @Override
   public final void run() {
      synchronized (this.this$0._reminderProviders) {
         boolean alreadyRegistered = false;

         for (int i = this.this$0._reminderProviders.length - 1; i >= 0; i--) {
            if (this.this$0._reminderProviders[i] == this._rmProvider) {
               alreadyRegistered = true;
               break;
            }
         }

         if (!alreadyRegistered) {
            Array.resize(this.this$0._reminderProviders, this.this$0._reminderProviders.length + 1);
            Array.resize(this.this$0._reminderProviderEnabled, this.this$0._reminderProviderEnabled.length + 1);
            Array.resize(this.this$0._reminders, this.this$0._reminderProviders.length + 1);
            this.this$0._reminderProviders[this.this$0._reminderProviders.length - 1] = this._rmProvider;
            this.this$0._reminders[this.this$0._reminderProviders.length - 1] = Long.MAX_VALUE;
            this.this$0._reminderProviderEnabled[this.this$0._reminderProviders.length - 1] = true;
            this.this$0._reminderScanner.notifyReminderProvider(this._rmProvider.getReminderProviderID());
         }
      }
   }
}

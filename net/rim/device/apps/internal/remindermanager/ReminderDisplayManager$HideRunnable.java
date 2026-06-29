package net.rim.device.apps.internal.remindermanager;

import net.rim.device.apps.api.reminders.ReminderElement;
import net.rim.device.apps.api.reminders.ReminderManager;

final class ReminderDisplayManager$HideRunnable implements Runnable {
   private ReminderElement _element;

   ReminderDisplayManager$HideRunnable(ReminderElement element) {
      this._element = element;
   }

   @Override
   public final void run() {
      try {
         ReminderDisplayManager.getInstance().syncHideReminderUI(this._element);
      } finally {
         ReminderManager.logEvent(1347572302, 2);
         return;
      }
   }
}

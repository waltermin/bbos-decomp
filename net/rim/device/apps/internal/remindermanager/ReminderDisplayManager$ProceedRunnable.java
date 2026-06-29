package net.rim.device.apps.internal.remindermanager;

import java.util.TimeZone;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderElement;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderProvider;

final class ReminderDisplayManager$ProceedRunnable implements Runnable {
   private ReminderElement _element;
   private boolean _doImmediateEvent;
   private long _snoozedTime = -1;

   ReminderDisplayManager$ProceedRunnable(ReminderElement element) {
      this._element = element;
   }

   ReminderDisplayManager$ProceedRunnable(ReminderElement element, boolean doImmediateEvent) {
      this(element);
      this._doImmediateEvent = doImmediateEvent;
   }

   ReminderDisplayManager$ProceedRunnable(ReminderElement element, boolean doImmediateEvent, long snoozedTime) {
      this(element, doImmediateEvent);
      this._snoozedTime = snoozedTime;
   }

   @Override
   public final void run() {
      ReminderDisplayManager rdm = ReminderDisplayManager.getInstance();

      try {
         if (this._snoozedTime != -1) {
            ReminderProvider reminderProvider = this._element.getReminderProvider();
            Reminder reminder = reminderProvider.getReminder(this._element.getReminderID());
            if (reminder != null) {
               long reminderTime = reminder.getReminderTime(TimeZone.getDefault());
               reminderProvider.updateReminderState(this._element.getReminderID(), 7, reminderTime);
               System.out.println(((StringBuffer)(new Object("Reminder was snoozed: "))).append(this._element.getReminderID()).toString());
            }
         }

         if (this._doImmediateEvent) {
            ReminderProvider reminderProvider = this._element.getReminderProvider();
            Reminder reminder = reminderProvider.getReminder(this._element.getReminderID());
            if (reminder == null) {
               ReminderManager.logEvent(1230130509, 3);
               return;
            }

            NotificationsManager.triggerImmediateEvent(reminderProvider.getProfileID(this._element.getReminderID()), 0, this._element, rdm.getContext());
         }

         rdm.addToQueue(this._element);
      } finally {
         ReminderManager.logEvent(1347572302, 2);
         NotificationsManager.cancelDeferredEvent(
            this._element.getReminderProvider().getProfileID(this._element.getReminderID()), 0, this._element, 1, rdm.getContext()
         );
         NotificationsManager.cancelImmediateEvent(this._element.getReminderProvider().getProfileID(this._element.getReminderID()), 0, this._element, null);
         return;
      }
   }
}

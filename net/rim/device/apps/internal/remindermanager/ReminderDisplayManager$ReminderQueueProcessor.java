package net.rim.device.apps.internal.remindermanager;

import java.util.TimeZone;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderElement;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderProvider;
import net.rim.device.apps.internal.api.quincy.QuincyManager;

final class ReminderDisplayManager$ReminderQueueProcessor extends Thread implements GlobalEventListener {
   private final ReminderDisplayManager this$0;

   ReminderDisplayManager$ReminderQueueProcessor(ReminderDisplayManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         synchronized (this.this$0._reminderQueue) {
            if (this.this$0._reminderBeingHandled != null) {
               this.this$0._proxy.invokeRunnable(new ReminderDisplayManager$HideRunnable(this.this$0._reminderBeingHandled));
               this.this$0._proxy.invokeRunnable(new ReminderDisplayManager$ProceedRunnable(this.this$0._reminderBeingHandled));
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Reminder reminder = null;
      ReminderProvider reminderProvider = null;

      while (true) {
         try {
            synchronized (this.this$0._reminderQueue) {
               while (this.this$0._reminderQueue.size() == 0 || this.this$0._reminderBeingHandled != null) {
                  try {
                     this.this$0._reminderQueue.wait();
                  } finally {
                     continue;
                  }
               }

               ReminderElement reminderElement = (ReminderElement)this.this$0._reminderQueue.elementAt(0);
               this.this$0.hideReminderUI(this.this$0._reminderBeingHandled);
               if (reminderElement != null) {
                  this.this$0._reminderQueue.removeElementAt(0);
                  reminderProvider = reminderElement.getReminderProvider();
                  reminder = reminderProvider.getReminder(reminderElement.getReminderID());
                  if (reminder == null) {
                     ReminderManager.logEvent(1364218445, 2);
                     NotificationsManager.cancelImmediateEvent(reminderProvider.getProfileID(reminderElement.getReminderID()), 0, reminderElement, null);
                     NotificationsManager.cancelDeferredEvent(
                        reminderProvider.getProfileID(reminderElement.getReminderID()), 0, reminderElement, 1, this.this$0._context
                     );
                     reminderProvider.reminderHandled(reminderElement.getReminderID());
                     this.this$0._reminderProviderBeingHandled = null;
                     this.this$0._reminderBeingHandled = null;
                  } else {
                     this.this$0._reminderBeingHandled = reminderElement;
                     this.this$0._reminderBeingHandledTime = reminder.getReminderTime(TimeZone.getDefault());
                     this.this$0._reminderProviderBeingHandled = reminderProvider;
                     if (!this.this$0.queueReminderDialog(reminder, reminderProvider, this.this$0._reminderBeingHandledTime)) {
                        NotificationsManager.cancelImmediateEvent(
                           reminderProvider.getProfileID(this.this$0._reminderBeingHandled.getReminderID()), 0, this.this$0._reminderBeingHandled, null
                        );
                        NotificationsManager.cancelDeferredEvent(
                           reminderProvider.getProfileID(this.this$0._reminderBeingHandled.getReminderID()),
                           0,
                           this.this$0._reminderBeingHandled,
                           1,
                           this.this$0._context
                        );
                        this.this$0._reminderProviderBeingHandled.reminderHandled(this.this$0._reminderBeingHandled.getReminderID());
                        this.this$0._reminderProviderBeingHandled = null;
                        this.this$0._reminderBeingHandled = null;
                     }
                  }
               }
            }

            this.this$0.processGlobalScreenQueue();
         } catch (Throwable var21) {
            Throwable t = var21;

            try {
               ReminderManager.logEvent(1380273989, 2);
               if (!(t instanceof OutOfMemoryError)) {
                  QuincyManager.sendJavaLogworthy("ReminderQueueProcessor");
               }

               ContextObject contextObject = new ContextObject();
               ContextObject.put(contextObject, -442409970680484936L, new Long(-7539463754156969819L));
               if (this.this$0._reminderBeingHandled != null && reminderProvider != null) {
                  NotificationsManager.cancelAllDeferredEvents(
                     reminderProvider.getProfileID(this.this$0._reminderBeingHandled.getReminderID()), 1, contextObject
                  );
               }

               if (reminderProvider != null) {
                  reminderProvider.resetUnhandledCount();
               }

               this.this$0._reminderProviderBeingHandled = null;
               this.this$0._reminderBeingHandled = null;
               continue;
            } finally {
               continue;
            }
         }
      }
   }
}

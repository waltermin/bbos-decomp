package net.rim.device.apps.internal.task;

import java.util.TimeZone;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.reminders.ReminderProvider;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.internal.proxy.Proxy;

public final class TaskReminderProvider implements ReminderProvider, CollectionListener {
   ReminderManager _reminderManager;
   TaskCollection _taskCollection;
   public static final long TASK_REMINDER_PROVIDER_ID;
   private static final int REMINDER_TIME_RADIUS;

   TaskReminderProvider(ReminderManager reminderManager, TaskCollection taskCollection) {
      this._reminderManager = reminderManager;
      this._taskCollection = taskCollection;
      this._reminderManager.registerReminderProvider(this);
   }

   @Override
   public final Reminder getNextReminder(long time, TimeZone tz) {
      int size = this._taskCollection.size();
      long nextReminderTime = Long.MAX_VALUE;
      Reminder nextReminder = null;

      for (int i = size - 1; i >= 0; i--) {
         Reminder r = (Reminder)this._taskCollection.getAt(i);
         int state = r.getReminderState();
         if (state != 3) {
            long reminderTime = r.getReminderTime(tz);
            if (reminderTime > time && reminderTime < nextReminderTime) {
               nextReminderTime = reminderTime;
               nextReminder = r;
            } else if (this.fireImmediatelyCheck(r, time)) {
               r = this.updateReminderState(r.getReminderID(), 6, reminderTime);
               this._reminderManager.immediateReminder(this, r);
            }
         }
      }

      return nextReminder;
   }

   @Override
   public final Reminder updateReminderState(long reminderID, int state, long reminderTime) {
      Reminder oldReminder = this.getReminder(reminderID);
      Reminder newReminder = oldReminder;
      if (ObjectGroup.isInGroup(oldReminder)) {
         newReminder = (Reminder)ObjectGroup.expandGroup(oldReminder);
      }

      if (newReminder == null) {
         return null;
      }

      ReminderModel rm = newReminder.getReminderData();
      if (rm == null) {
         return null;
      }

      rm.setState(state);
      rm.setReminderFiredFor(reminderTime);
      newReminder.updateReminderData(rm);
      ObjectGroup.createGroupIgnoreTooBig(newReminder);
      this._taskCollection.update(oldReminder, newReminder);
      return newReminder;
   }

   @Override
   public final Reminder getReminder(long id) {
      synchronized (this._taskCollection) {
         int size = this._taskCollection.size();

         for (int i = size - 1; i >= 0; i--) {
            Reminder r = (Reminder)this._taskCollection.getAt(i);
            if (r.getReminderID() == id) {
               return r;
            }
         }

         return null;
      }
   }

   @Override
   public final long getReminderProviderID() {
      return 3036768504489783563L;
   }

   @Override
   public final long getProfileID(long reminderID) {
      return 204325571560529255L;
   }

   @Override
   public final void reminderQueued(long ReminderID) {
      UnreadCountManager.incrementUnreadCount(9);
   }

   @Override
   public final void reminderHandled(long ReminderID) {
      if (UnreadCountManager.getUnreadCount(9) > 0) {
         UnreadCountManager.decrementUnreadCount(9);
      }
   }

   private final void checkForUnreadCountMismatch() {
      int deferedEventCount = NotificationsManager.getDeferredEventCount(204325571560529255L);
      int unReadCount = UnreadCountManager.getUnreadCount(9);
      if (deferedEventCount != unReadCount) {
         EventLogger.logEvent(-1576052272418032312L, 1380796755, 2);

         while (deferedEventCount != unReadCount) {
            if (unReadCount < deferedEventCount) {
               UnreadCountManager.incrementUnreadCount(9);
               unReadCount++;
            } else {
               UnreadCountManager.decrementUnreadCount(9);
               unReadCount--;
            }
         }
      }
   }

   @Override
   public final long getSnoozeAmount() {
      return TaskOptions.getOptions().getSnoozeMillis();
   }

   @Override
   public final EncodedImage getReminderIcon() {
      return ThemeManager.getActiveTheme().getImage("net_rim_task_reminder");
   }

   @Override
   public final void displayReminderIndicator(boolean showIndicator) {
      if (showIndicator) {
         this.checkForUnreadCountMismatch();
      }

      UnreadCountManager.setUnreadCountVisible(9, showIndicator);
   }

   @Override
   public final String getName() {
      return "TASK";
   }

   @Override
   public final void resetUnhandledCount() {
      while (UnreadCountManager.getUnreadCount(9) > 0) {
         UnreadCountManager.decrementUnreadCount(9);
      }
   }

   @Override
   public final int getUnhandledCount() {
      return UnreadCountManager.getUnreadCount(9);
   }

   @Override
   public final Object[] getUnhandledReminders() {
      return NotificationsManager.getDeferredEvents(204325571560529255L);
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      long adjustedTime = this._reminderManager.getAdjustedCurrentTimeMillis();
      Reminder r = (Reminder)element;
      if (this.fireImmediatelyCheck(r, adjustedTime)) {
         Proxy.getInstance().invokeLater(new TaskReminderProvider$FireReminderRunnable(this, r, -1));
      }

      this._reminderManager.addReminder(this, r);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      long adjustedTime = this._reminderManager.getAdjustedCurrentTimeMillis();
      Reminder r = (Reminder)newElement;
      if (this.fireImmediatelyCheck(r, adjustedTime)) {
         Proxy.getInstance().invokeLater(new TaskReminderProvider$FireReminderRunnable(this, r, -1));
      }

      this._reminderManager.updateReminder(this, (Reminder)newElement, (Reminder)oldElement);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this._reminderManager.removeReminder(this, (Reminder)element);
   }

   private final boolean fireImmediatelyCheck(Reminder r, long time) {
      TimeZone tz = TimeZone.getDefault();
      int reminderState = r.getReminderState();
      long start = r.getReminderTime(tz) - 30000;
      long end = r.getReminderEndTime(tz) + 30000;
      return time >= start && time <= end && (reminderState == 1 || reminderState == 4);
   }
}

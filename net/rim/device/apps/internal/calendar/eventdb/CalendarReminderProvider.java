package net.rim.device.apps.internal.calendar.eventdb;

import java.util.TimeZone;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.reminders.ReminderProvider;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;

public final class CalendarReminderProvider implements CollectionListener, ReminderProvider {
   private ReminderManager _reminderManager;
   private CalDB _calendardb;
   private long _providerID;
   private long _nearestFuture = Long.MAX_VALUE;
   private long _nearestEventLUID;
   private boolean _nearestFutureSet;
   private Recur$Handle _sharedHandle = new Recur$Handle();
   private static final int REMINDER_COUNT_MISMATCH = 1380796755;
   private static boolean _markAllPendingForRetry = true;

   public CalendarReminderProvider(ReminderManager reminderManager, CalDB calendardb) {
      this._reminderManager = reminderManager;
      this._calendardb = calendardb;
      this._providerID = calendardb.getCalendarServiceID();
      this.resetUnhandledCount();
      this._reminderManager.registerReminderProvider(this);
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (element instanceof Reminder) {
         long adjustedTime = this._reminderManager.getAdjustedCurrentTimeMillis();
         TimeZone tz = TimeZone.getDefault();
         Reminder r = this.getNextReminderWorker(adjustedTime, tz, (Event)element);
         if (r != null) {
            this._reminderManager.addReminder(this, r);
         }
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (oldElement instanceof Reminder) {
         Reminder oldReminder = (Reminder)oldElement;
         Reminder newReminder = (Reminder)newElement;
         Event oldEvent = (Event)oldElement;
         Event newEvent = (Event)newElement;
         ReminderModel rm = newEvent.getReminderData();
         if (rm != null) {
            long reminderDelta = rm.getTime();
            int reminderState = newReminder.getReminderState();
            long adjustedTime = this._reminderManager.getAdjustedCurrentTimeMillis();
            TimeZone tz = TimeZone.getDefault();
            if (oldEvent.isRecurring()) {
               this._reminderManager.removeReminder(this, oldReminder);
               if (reminderState == 1 || reminderState == 4) {
                  newReminder = this.markPreviousInstanceFired(newReminder, adjustedTime - reminderDelta, tz);
               }

               this._reminderManager.addReminder(this, newReminder);
               return;
            }

            long startTime = newEvent.getStartDate(tz);
            long endTime = newEvent.getDuration(tz);
            long reminderPoint = startTime - reminderDelta;
            if (endTime < 60000) {
               endTime = 60000;
            }

            endTime += startTime;
            if (rm.hasReminder() && reminderPoint <= adjustedTime && adjustedTime <= endTime && (reminderState == 1 || reminderState == 4)) {
               newEvent = (Event)this.updateReminderState(newEvent.getReminderID(), 6, reminderPoint);
               this._reminderManager.immediateReminder(this, newEvent);
               return;
            }

            if (reminderState != 6 && reminderState != 3) {
               this._reminderManager.updateReminder(this, newReminder, oldReminder);
            }
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof Reminder) {
         Reminder r = (Reminder)element;
         this._reminderManager.removeReminder(this, r);
      }
   }

   @Override
   public final Reminder getNextReminder(long time, TimeZone tz) {
      return this.getNextReminderWorker(time, tz, null);
   }

   private final Reminder getNextReminderWorker(long time, TimeZone tz, Event updatedEvent) {
      synchronized (this._calendardb.getLockObject()) {
         Reminder result = null;
         this._nearestFuture = Long.MAX_VALUE;
         this._nearestEventLUID = 0;
         this._nearestFutureSet = false;
         int max = this._calendardb.size();
         Object[] calObjects = new Object[max];
         this._calendardb.getElements(calObjects);
         if (updatedEvent == null) {
            for (int i = 0; i < max; i++) {
               Object o = calObjects[i];
               if (o instanceof Event) {
                  Event event = (Event)o;
                  this.examineEvent(event, time, tz);
               }
            }
         } else {
            this.examineEvent(updatedEvent, time, tz);
         }

         if (this._nearestFutureSet) {
            Reminder nearestEvent = (Reminder)this._calendardb.get(this._nearestEventLUID);
            if (nearestEvent != null) {
               result = this.markPreviousInstanceFired(nearestEvent, this._nearestFuture, tz);
               this._nearestEventLUID = ((Event)result).getLUID();
            } else {
               this._nearestEventLUID = 0;
            }
         }

         _markAllPendingForRetry = false;
         return result;
      }
   }

   private final Reminder markPreviousInstanceFired(Reminder instance, long currentTime, TimeZone tz) {
      Reminder result = instance;
      if (((Event)instance).isRecurring()) {
         Recur$Handle handle = new Recur$Handle();
         ReminderModel rm = instance.getReminderData();
         if (rm != null) {
            long reminderDelta = rm.getTime();
            boolean validEventInstance = ((Event)instance).getHandleBeforeTime(handle, currentTime + reminderDelta, tz);
            if (validEventInstance && rm.getReminderFiredFor() < handle._handle) {
               result = this.updateReminderState(instance.getReminderID(), 3, handle._handle - rm.getTime());
            }
         }
      }

      return result;
   }

   private final void examineEvent(Event event, long adjustedCurrentTime, TimeZone tz) {
      int state = 0;
      if (event != null) {
         ReminderModel rm = event.getReminderData();
         if (rm != null && rm.hasReminder()) {
            long reminderDelta = rm.getTime();
            state = rm.getState();
            if (state == 2 || state == 5 || state == 6) {
               if (!_markAllPendingForRetry) {
                  return;
               }

               if (!event.isRecurring()) {
                  int var12 = 4;
                  event = (Event)this.updateReminderState(event.getLUID(), 4, event.getStartDate(tz) - reminderDelta);
               } else {
                  long lastReminderFired = Long.MIN_VALUE;
                  int var13 = 4;
                  event = (Event)this.updateReminderState(event.getLUID(), 4, lastReminderFired);
               }
            }

            if (event.isRecurring()) {
               this.examineRecurring(event, rm, reminderDelta, adjustedCurrentTime, tz);
            } else {
               this.examineSingle(event, rm, reminderDelta, adjustedCurrentTime, tz);
            }
         }
      }
   }

   private final void examineSingle(Event event, ReminderModel rm, long reminderDelta, long adjustedCurrentTime, TimeZone tz) {
      int reminderState = 0;
      long startTime = event.getStartDate(tz);
      long endTime = event.getDuration(tz);
      long reminderPoint = startTime - reminderDelta;
      if (endTime < 60000) {
         endTime = 60000;
      }

      endTime += startTime;
      reminderState = rm.getState();
      if (reminderPoint > adjustedCurrentTime || adjustedCurrentTime > endTime || reminderState != 1 && reminderState != 4) {
         if (reminderPoint > adjustedCurrentTime && reminderState != 3 && this._nearestFuture > reminderPoint) {
            this._nearestFutureSet = true;
            this._nearestFuture = reminderPoint;
            this._nearestEventLUID = event.getLUID();
         }
      } else {
         event = (Event)this.updateReminderState(event.getReminderID(), 6, this._sharedHandle._handle - rm.getTime());
         this._reminderManager.immediateReminder(this, event);
      }
   }

   private final void examineRecurring(Event event, ReminderModel rm, long reminderDelta, long adjustedCurrentTime, TimeZone tz) {
      boolean validEventInstance = event.getHandleBeforeTime(this._sharedHandle, adjustedCurrentTime + reminderDelta, tz);
      if (validEventInstance) {
         long startTime = event.getStartFromHandle(this._sharedHandle._handle, tz);
         long endTime = event.getDurationFromHandle(this._sharedHandle._handle, tz);
         long reminderPoint = startTime - reminderDelta;
         if (endTime < 60000) {
            endTime = 60000;
         }

         endTime += startTime;
         int state = rm.getState();
         if (reminderPoint <= adjustedCurrentTime && adjustedCurrentTime <= endTime && (rm.getReminderFiredFor() < this._sharedHandle._handle || state == 4)) {
            long reminderTimer = this._sharedHandle._handle - reminderDelta;
            event = (Event)this.updateReminderState(event.getReminderID(), 6, reminderTimer);
            this._reminderManager.immediateReminder(this, event);
            return;
         }
      }

      validEventInstance = true;
      long lastInstanceFound = -1;

      while (validEventInstance) {
         if (event.getAllDayFlag()) {
            validEventInstance = event.getHandleAfterTime(this._sharedHandle, adjustedCurrentTime + reminderDelta + 86400000, tz);
            adjustedCurrentTime += 86400000;
         } else {
            validEventInstance = event.getHandleAfterTime(this._sharedHandle, adjustedCurrentTime + reminderDelta, tz);
         }

         if (!validEventInstance) {
            return;
         }

         if (this._sharedHandle._handle == lastInstanceFound) {
            return;
         }

         lastInstanceFound = this._sharedHandle._handle;
         long startTime = event.getStartFromHandle(this._sharedHandle._handle, tz);
         long reminderPoint = startTime - reminderDelta;
         if (reminderPoint <= adjustedCurrentTime) {
            validEventInstance = event.getHandleAfter(this._sharedHandle, this._sharedHandle._handle);
            if (!validEventInstance) {
               return;
            }

            startTime = event.getStartFromHandle(this._sharedHandle._handle, tz);
            reminderPoint = startTime - reminderDelta;
         }

         if (rm.getReminderFiredFor() < this._sharedHandle._handle) {
            if (this._nearestFuture > reminderPoint) {
               this._nearestFutureSet = true;
               this._nearestFuture = reminderPoint;
               this._nearestEventLUID = event.getLUID();
            }

            validEventInstance = false;
         } else if (this._sharedHandle._handle > adjustedCurrentTime) {
            adjustedCurrentTime = this._sharedHandle._handle;
         } else {
            validEventInstance = false;
         }
      }
   }

   @Override
   public final Reminder updateReminderState(long reminderID, int state, long reminderTime) {
      Reminder result = null;
      Event event = null;
      long eventLUID = reminderID;
      event = (Event)this._calendardb.get(eventLUID);
      if (event != null) {
         event = (Event)ObjectGroup.expandGroup(event);
         if (event == null) {
            return null;
         }

         ReminderModel rmodel = event.getReminderData();
         if (rmodel == null) {
            return null;
         }

         result = event;
         if (!event.isRecurring()) {
            rmodel.setState(state);
         } else {
            if (state == 2 || state == 5 || state == 6 || state == 4 || state == 3) {
               rmodel.setReminderFiredFor(reminderTime + rmodel.getTime());
            }

            if (state == 3) {
               rmodel.setState(1);
            } else {
               rmodel.setState(state);
            }
         }

         event.updateReminderData(rmodel);
         if (state != 5 && state != 6) {
            this._calendardb.addWithAction(event, 2);
         } else {
            this._calendardb.addWithAction(event, 6);
         }
      }

      return result;
   }

   @Override
   public final Reminder getReminder(long reminderID) {
      return (Reminder)this._calendardb.get(reminderID);
   }

   @Override
   public final long getReminderProviderID() {
      return this._providerID;
   }

   @Override
   public final long getProfileID(long reminderID) {
      return 2666833733215697856L;
   }

   @Override
   public final void reminderQueued(long reminderID) {
      UnreadCountManager.incrementUnreadCount(8);
   }

   @Override
   public final void reminderHandled(long reminderID) {
      if (UnreadCountManager.getUnreadCount(8) > 0) {
         UnreadCountManager.decrementUnreadCount(8);
      }
   }

   @Override
   public final void resetUnhandledCount() {
      while (UnreadCountManager.getUnreadCount(8) > 0) {
         UnreadCountManager.decrementUnreadCount(8);
      }
   }

   @Override
   public final int getUnhandledCount() {
      return UnreadCountManager.getUnreadCount(8);
   }

   @Override
   public final void displayReminderIndicator(boolean showIndicator) {
      if (showIndicator) {
         this.checkForUnreadCountMismatch();
      }

      UnreadCountManager.setUnreadCountVisible(8, showIndicator);
   }

   @Override
   public final long getSnoozeAmount() {
      return CalendarOptions.getOptions().getSnoozeMillis();
   }

   @Override
   public final EncodedImage getReminderIcon() {
      return ThemeManager.getActiveTheme().getImage("net_rim_calendar_reminder");
   }

   @Override
   public final String getName() {
      return "CAL";
   }

   @Override
   public final Object[] getUnhandledReminders() {
      return NotificationsManager.getDeferredEvents(2666833733215697856L);
   }

   private final void checkForUnreadCountMismatch() {
      int deferedEventCount = NotificationsManager.getDeferredEventCount(2666833733215697856L);
      int unReadCount = UnreadCountManager.getUnreadCount(8);
      if (deferedEventCount != unReadCount) {
         EventLogger.logEvent(-256469206327664059L, 1380796755, 4);

         while (deferedEventCount != unReadCount) {
            if (unReadCount < deferedEventCount) {
               UnreadCountManager.incrementUnreadCount(8);
               unReadCount++;
            } else {
               UnreadCountManager.decrementUnreadCount(8);
               unReadCount--;
            }
         }
      }
   }
}

package net.rim.device.apps.internal.task;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.utility.framework.RecurCalc;
import net.rim.device.cldc.util.CalendarExtensions;

public final class TaskUtilities implements GlobalEventListener {
   public static final byte TASKID = 116;
   public static final int RECORDID = 1;
   public static final int NAME = 2;
   public static final int NOTES = 3;
   public static final int DUE_DATE = 5;
   public static final int START_DATE = 6;
   public static final int DUE = 8;
   public static final int STATUS = 9;
   public static final int PRIORITY = 10;
   public static final int REMINDER_TYPE_FIELD_ID = 14;
   public static final int REMINDER_DATE_TIME_FIELD_ID = 15;
   public static final int TIME_ZONE_FIELD_ID = 16;
   public static final int REMINDER_STATE_FIELD_ID = 18;
   private static RecurCalc recurrenceCalculator = new RecurCalc();
   private static final long TASK_FIELD_LISTENER_INSTANCE = -2888269685515488385L;
   static Calendar _gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
   static Calendar _localCalendar = Calendar.getInstance();
   static DateFormat _dateAndTimeFormat = DateFormat.getInstance(46);

   static final TaskUtilities getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      TaskUtilities listener = (TaskUtilities)ar.getOrWaitFor(-2888269685515488385L);
      if (listener == null) {
         listener = new TaskUtilities();
         ar.put(-2888269685515488385L, listener);
      }

      return listener;
   }

   private TaskUtilities() {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3596208183088439728L) {
         synchronized (_localCalendar) {
            _localCalendar.setTimeZone(TimeZone.getDefault());
            _gmtCalendar.set(0, 1);
            _localCalendar.set(0, 1);
         }
      }
   }

   static final CICALConfiguration getCICALConfiguration() {
      ServiceRecord[] syncSRs = ServiceBook.getSB().findRecordsByCid("sync");
      if (syncSRs.length > 0) {
         ServiceRecord cicalSR = ServiceBook.getSB().getCIDAssociatedWithService("CICAL", syncSRs[0]);
         if (cicalSR != null) {
            return CalendarServiceManager.getInstance().findCalendarServiceByKey(cicalSR).getCICALConfiguration();
         }
      }

      return CalendarServiceManager.getInstance().getDefaultCalendarService().getCICALConfiguration();
   }

   static final long convertToMidnight(long date, int dayIncrement) {
      synchronized (_localCalendar) {
         ((CalendarExtensions)_localCalendar).setTimeLong(date);
         _localCalendar.setTimeZone(TimeZone.getDefault());
         DateTimeUtilities.zeroCalendarTime(_localCalendar);
         _gmtCalendar.set(0, 1);
         _localCalendar.set(0, 1);
         if (dayIncrement > 0) {
            int newday = _localCalendar.get(5) + dayIncrement;
            _localCalendar.set(5, newday);
         }

         return ((CalendarExtensions)_localCalendar).getTimeLong();
      }
   }

   static final long convertToMidnightGMT(long date, int dayIncrement) {
      synchronized (_localCalendar) {
         _localCalendar.setTimeZone(TimeZone.getDefault());
         ((CalendarExtensions)_localCalendar).setTimeLong(date);
         _gmtCalendar.set(0, 1);
         _localCalendar.set(0, 1);
         _gmtCalendar.set(1, _localCalendar.get(1));
         _gmtCalendar.set(2, _localCalendar.get(2));
         int day = _localCalendar.get(5);
         if (dayIncrement > 0) {
            day += dayIncrement;
         }

         DateTimeUtilities.zeroCalendarTime(_gmtCalendar);
         _gmtCalendar.set(5, day);
         return _gmtCalendar.getTime().getTime();
      }
   }

   static final long convertToGMT(long date) {
      synchronized (_localCalendar) {
         _localCalendar.setTimeZone(TimeZone.getDefault());
         ((CalendarExtensions)_localCalendar).setTimeLong(date);
         _gmtCalendar.set(0, 1);
         _localCalendar.set(0, 1);
         _gmtCalendar.set(1, _localCalendar.get(1));
         _gmtCalendar.set(2, _localCalendar.get(2));
         _gmtCalendar.set(5, _localCalendar.get(5));
         _gmtCalendar.set(11, _localCalendar.get(11));
         _gmtCalendar.set(12, _localCalendar.get(12));
         _gmtCalendar.set(13, _localCalendar.get(13));
         _gmtCalendar.set(14, _localCalendar.get(14));
         return ((CalendarExtensions)_gmtCalendar).getTimeLong();
      }
   }

   static final long convertFromGMT(long date, TimeZone destTimeZone) {
      synchronized (_localCalendar) {
         _localCalendar.setTimeZone(destTimeZone);
         ((CalendarExtensions)_gmtCalendar).setTimeLong(date);
         _gmtCalendar.set(0, 1);
         _localCalendar.set(0, 1);
         _localCalendar.set(1, _gmtCalendar.get(1));
         _localCalendar.set(2, _gmtCalendar.get(2));
         _localCalendar.set(5, _gmtCalendar.get(5));
         _localCalendar.set(11, _gmtCalendar.get(11));
         _localCalendar.set(12, _gmtCalendar.get(12));
         _localCalendar.set(13, _gmtCalendar.get(13));
         _localCalendar.set(14, _gmtCalendar.get(14));
         return ((CalendarExtensions)_localCalendar).getTimeLong();
      }
   }

   static final TaskModelImpl advanceTaskRecurrence(TaskModelImpl recurringTask, boolean deleteOld, int status, TaskModelImpl originalRecurringTask) {
      TaskModelImpl oldTask = null;
      Recur recurInfo = recurringTask.getRecurrenceModel();
      if (recurInfo != null && recurInfo.getRecurType() != 0) {
         TaskCollectionImpl taskCollection = TaskCollectionImpl.getInstance();
         Recur$Handle handle = new Recur$Handle();
         long recurrenceStart = recurringTask.getRecurrenceStartDate();
         boolean validInstance = false;
         synchronized (_localCalendar) {
            _gmtCalendar.set(0, 1);
            _localCalendar.set(0, 1);
            validInstance = recurrenceCalculator.getAnInstance(recurrenceStart + 1, true, handle, recurrenceStart, 0, recurInfo, _gmtCalendar.getTimeZone());
         }

         if (validInstance) {
            long taskStart = recurringTask.getStartDate();
            long delta = handle._handle - recurrenceStart;
            if (!getCICALConfiguration().isInfiniteRecurrenceAllowed()) {
               oldTask = recurringTask;
               TaskModelImpl newTaskModel = (TaskModelImpl)recurringTask.getNextInstance();
               boolean nextDateInclusion = false;
               boolean startDateInclusion = false;
               Recur newRecurInfo = newTaskModel.getRecurrenceModel();
               if (newRecurInfo.getInclusionCount() > 0) {
                  long[] inclusions = newRecurInfo.getInclusions(null);
                  if (Arrays.binarySearch(inclusions, handle._handle, 0, inclusions.length) >= 0) {
                     nextDateInclusion = true;
                  }

                  if (Arrays.binarySearch(inclusions, taskStart, 0, inclusions.length) >= 0) {
                     startDateInclusion = true;
                  }
               }

               if (startDateInclusion) {
                  newRecurInfo.removeInclusion(recurrenceStart);
               } else if (nextDateInclusion) {
                  newRecurInfo.addExclusion(recurrenceStart);
               } else {
                  newTaskModel.setRecurrenceStartDate(handle._handle);
               }

               newTaskModel.setStartDate(recurringTask.getStartDate() + delta);
               long dueTime = newTaskModel.getDueDate() + delta;
               newTaskModel.setDueDate(dueTime);
               updateReminder(newTaskModel, delta);
               if (!ObjectGroup.isInGroup(newTaskModel)) {
                  ObjectGroup.createGroupIgnoreTooBig(newTaskModel);
               }

               taskCollection.add(newTaskModel);
               if (!deleteOld) {
                  Recur oldRecurInfo = oldTask.getRecurrenceModel();
                  oldRecurInfo.setRecurType((byte)0);
                  oldTask.setStatus(status);
               }
            } else {
               if (!deleteOld) {
                  TaskModel newTaskModel = recurringTask.getNextInstance();
                  newTaskModel.setStatus(status);
                  if (status == 2) {
                     newTaskModel.getReminderModel().setTime(-1);
                     newTaskModel.getRecurrenceModel().setRecurType((byte)0);
                  }

                  if (!ObjectGroup.isInGroup(newTaskModel)) {
                     ObjectGroup.createGroupIgnoreTooBig(newTaskModel);
                  }

                  taskCollection.add(newTaskModel);
                  oldTask = (TaskModelImpl)newTaskModel;
               }

               TaskModelImpl updatedTaskModel = null;
               if (ObjectGroup.isInGroup(recurringTask)) {
                  updatedTaskModel = (TaskModelImpl)ObjectGroup.expandGroup(recurringTask);
               } else {
                  updatedTaskModel = recurringTask;
               }

               updatedTaskModel.setStartDate(taskStart + delta);
               updatedTaskModel.setRecurrenceStartDate(handle._handle);
               updatedTaskModel.setStatus(0);
               updatedTaskModel.setDueDate(updatedTaskModel.getDueDate() + delta);
               updateReminder(updatedTaskModel, delta);
               ObjectGroup.createGroupIgnoreTooBig(updatedTaskModel);
               if (originalRecurringTask != null) {
                  taskCollection.update(originalRecurringTask, updatedTaskModel);
               } else {
                  taskCollection.update(recurringTask, updatedTaskModel);
               }
            }
         }

         return oldTask;
      } else {
         return null;
      }
   }

   private static final void updateReminder(TaskModelImpl task, long delta) {
      ReminderModel rm = task.getReminderData();
      if (rm != null && rm.hasReminder()) {
         rm.setState(1);
         rm.setReminderFiredFor(Long.MIN_VALUE);
         if (rm.getType() == 2) {
            TimeZone tz = TimeZone.getTimeZone(task.getTimeZoneID());
            long oldReminderTime = task.getReminderTime(_gmtCalendar.getTimeZone());
            long reminderTime = oldReminderTime + delta;
            synchronized (_localCalendar) {
               _localCalendar.setTimeZone(tz);
               ((CalendarExtensions)_localCalendar).setTimeLong(oldReminderTime);
               int oldHour = _localCalendar.get(11);
               ((CalendarExtensions)_localCalendar).setTimeLong(reminderTime);
               _localCalendar.set(11, oldHour);
               reminderTime = ((CalendarExtensions)_localCalendar).getTimeLong();
            }

            rm.setTime(convertToGMT(reminderTime));
         }
      }
   }

   static final boolean toggleStatus(TaskModel task) {
      if (task != null && task.getStatus() != -1) {
         int newStatus = task.isCompleted() ? 1 : 2;
         ChangeStatusVerb.getInstance(task, 0, 0, newStatus).invoke(null);
         return true;
      } else {
         return false;
      }
   }
}

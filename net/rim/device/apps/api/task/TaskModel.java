package net.rim.device.apps.api.task;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;

public interface TaskModel extends ReadableList, WritableSet, PersistableRIMModel {
   int STATUS_NOT_STARTED = 0;
   int STATUS_IN_PROGRESS = 1;
   int STATUS_COMPLETED = 2;
   int STATUS_WAITING = 3;
   int STATUS_DEFERRED = 4;
   int PRIORITY_HIGH = 0;
   int PRIORITY_NORMAL = 1;
   int PRIORITY_LOW = 2;
   long INVALID_DATE = Long.MIN_VALUE;

   int getPriority();

   void setPriority(int var1);

   int getStatus();

   void setStatus(int var1);

   boolean isCompleted();

   long getDueDate();

   void setDueDate(long var1);

   boolean hasDueDate();

   String getNotes();

   void setNotes(String var1);

   RIMModel getTitleModel();

   CategoriesModel getCategoriesModel();

   ReminderModel getReminderModel();

   void setReminderModel(ReminderModel var1);

   Recur getRecurrenceModel();

   void setRecurrenceModel(Recur var1);

   long getRecurrenceStartDate();

   void setRecurrenceStartDate(long var1);

   long getStartDate();

   void setStartDate(long var1);

   String getTimeZoneID();

   void setTimeZoneID(String var1);

   boolean isRecurring();

   int getUID();
}

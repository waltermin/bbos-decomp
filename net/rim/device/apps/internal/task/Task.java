package net.rim.device.apps.internal.task;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.model.ContextObject;

final class Task {
   private TaskCollectionImpl _taskCollection = TaskCollectionImpl.getInstance();
   private TaskUICollection _taskUICollection = new TaskUICollection(this._taskCollection);
   private TaskStatusAndKeywordFilterList _keyList;
   static final int TASK_DB_VERSION;
   public static final long EVENT_LOGGER_GUID;
   public static final String EVENT_LOGGER_NAME;
   static final int TASK_CORRUPT_TASK_DATA;
   static final int TASK_NOT_A_TASK_OBJECT;
   static final int TASK_ERROR_CONVERTING_DATA;
   static final int TASK_INVALID_STATUS;
   static final int TASK_DATA_MISSING;
   static final int REMINDER_COUNT_MISMATCH;
   static final long ID;

   private Task() {
   }

   final TaskUICollection getUICollection() {
      return this._taskUICollection;
   }

   final TaskStatusAndKeywordFilterList getFilteredList() {
      int currentOrder = TaskOptions.getOptions().getSortOrderIndex();
      if (currentOrder != this._taskCollection.getCurrentOrderIndex()) {
         this._taskCollection.endSyncCleanup();
      }

      if (this._keyList == null) {
         this._keyList = new TaskStatusAndKeywordFilterList(
            this.getUICollection(), (KeywordIndexerHelper)(new Object(new TaskComparator(), (ContextObject)(new Object(28)))), false
         );
      }

      return this._keyList;
   }

   final Screen getUI() {
      return TaskList.getInstance();
   }

   static final Task getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Task t = (Task)ar.getOrWaitFor(-6809143763226991117L);
      if (t == null) {
         t = new Task();
         ar.put(-6809143763226991117L, t);
      }

      return t;
   }
}

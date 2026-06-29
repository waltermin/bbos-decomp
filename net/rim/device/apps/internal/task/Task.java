package net.rim.device.apps.internal.task;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.model.ContextObject;

final class Task {
   private TaskCollectionImpl _taskCollection = TaskCollectionImpl.getInstance();
   private TaskUICollection _taskUICollection = new TaskUICollection(this._taskCollection);
   private TaskStatusAndKeywordFilterList _keyList;
   static final int TASK_DB_VERSION = 2;
   public static final long EVENT_LOGGER_GUID = -1576052272418032312L;
   public static final String EVENT_LOGGER_NAME = "net.rim.tasks";
   static final int TASK_CORRUPT_TASK_DATA = 1129469264;
   static final int TASK_NOT_A_TASK_OBJECT = 1312904271;
   static final int TASK_ERROR_CONVERTING_DATA = 1162040900;
   static final int TASK_INVALID_STATUS = 1229870675;
   static final int TASK_DATA_MISSING = 1313817665;
   static final int REMINDER_COUNT_MISMATCH = 1380796755;
   static final long ID = -6809143763226991117L;

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

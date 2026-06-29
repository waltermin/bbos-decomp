package net.rim.device.apps.internal.task;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.framework.model.RIMModelSyncConverter;
import net.rim.device.apps.api.pim.TimeBasedObjectProvider;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentSyncCollection$SimpleData;
import net.rim.device.apps.internal.task.resources.TaskResources;
import net.rim.device.internal.proxy.Proxy;

final class TaskCollectionImpl
   extends SimplePersistentEncryptedSyncCollection
   implements TaskCollection,
   OTASyncCapable,
   OTASyncPriorityProvider,
   TimeBasedObjectProvider {
   private SimplePersistentSyncCollection$SimpleData _data;
   private int _currentOrder;
   private boolean _dirty;
   private TaskCollectionImpl$MirroredTaskCollection _mirroredCollection;
   private static final int TASK_INITIAL_SIZE = 16;
   private static final long TASK_DATA_NAME = -2395935032978087656L;
   private static final long TASKCOLLECTION_ID = 2054667582385114214L;
   private static RIMModelSyncConverter _syncConverter;

   final synchronized int getCurrentOrderIndex() {
      return this._currentOrder;
   }

   public final boolean isDirty() {
      return this._dirty;
   }

   public final void markDirty(boolean dirty) {
      if (!TaskOptions.getOptions().isWirelessSyncAllowed()) {
         this._dirty = dirty;
      } else {
         dirty = false;
      }
   }

   @Override
   public final TaskCollection getCollectionStore() {
      return this;
   }

   @Override
   public final int getSyncPriority() {
      return 8;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final void getElementsVisibleDuring(long start, long duration, TimeZone tz, SimpleSortingVector timeBasedObjectVector, boolean forceSort) {
      synchronized (this._mirroredCollection) {
         for (int i = 0; i < this._mirroredCollection.size(); i++) {
            TaskModelImpl task = (TaskModelImpl)this._mirroredCollection.getAt(i);
            if (task.hasDueDate()) {
               long dueDate = TaskUtilities.convertFromGMT(task.getDueDate(), tz);
               if (dueDate >= start && dueDate < start + duration) {
                  timeBasedObjectVector.addElement(task);
               }
            }
         }

         if (forceSort) {
            timeBasedObjectVector.setSortComparator(this.getComparator());
            timeBasedObjectVector.reSort();
            timeBasedObjectVector.setSortComparator(null);
         }
      }
   }

   @Override
   public final void getElementsVisibleDuring(long start, long duration, TimeZone tz, SimpleSortingVector timeBasedObjectVector) {
      this.getElementsVisibleDuring(start, duration, tz, timeBasedObjectVector, false);
   }

   @Override
   public final long[] getElementsStartingAround(long time, int maxBefore, int maxOnOrAfter, TimeZone tz, Vector eventVector) {
      synchronized (this._mirroredCollection) {
         int beforeCount = 0;
         int afterOrOnCount = 0;
         int staringIndex = this._mirroredCollection.findNearestTo(time, tz);
         int OnAfterPoint = staringIndex;
         if (staringIndex != -1) {
            while (staringIndex > 0 && beforeCount < maxBefore) {
               TaskModelImpl task = (TaskModelImpl)this._mirroredCollection.getAt(--staringIndex);
               if (task.hasDueDate()) {
                  beforeCount++;
               }
            }

            for (int i = staringIndex; i < this._mirroredCollection.size() && afterOrOnCount < maxOnOrAfter; i++) {
               TaskModelImpl task = (TaskModelImpl)this._mirroredCollection.getAt(i);
               if (task.hasDueDate()) {
                  eventVector.addElement(task);
                  if (i >= OnAfterPoint) {
                     afterOrOnCount++;
                  }
               }
            }
         }

         int total = beforeCount + afterOrOnCount;
         long startDate = time;
         long endDate = time;
         if (eventVector.size() > 0) {
            TaskModelImpl task = (TaskModelImpl)eventVector.firstElement();
            startDate = task.getDueDate();
            task = (TaskModelImpl)eventVector.lastElement();
            endDate = task.getDueDate();
         }

         return new long[]{beforeCount, afterOrOnCount, total > maxBefore + maxOnOrAfter ? total : 0, startDate, endDate};
      }
   }

   @Override
   public final String getProviderName() {
      return TaskResources.getString(41);
   }

   @Override
   public final long getProviderID() {
      return -5718599435502913979L;
   }

   @Override
   public final int getEventCountBeforeTime(long time, TimeZone tz) {
      int beforeCount = 0;
      synchronized (this._mirroredCollection) {
         int size = this._mirroredCollection.size();
         int index = this._mirroredCollection.findNearestTo(time, tz);
         if (index > 0 && index < size) {
            TaskModelImpl task = (TaskModelImpl)this.getAt(index);
            if (task.hasDueDate()) {
               long dueDate = TaskUtilities.convertFromGMT(task.getDueDate(), tz);
               if (dueDate > time) {
                  index--;
               }
            }

            if (index > 0) {
               beforeCount = index;
            }
         }

         return beforeCount;
      }
   }

   @Override
   public final int getEventCountAfterTime(long time, TimeZone tz) {
      int afterCount = 0;
      synchronized (this._mirroredCollection) {
         int size = this._mirroredCollection.size();
         int index = this._mirroredCollection.findNearestTo(time, tz);
         if (index > 0 && index < size) {
            TaskModelImpl task = (TaskModelImpl)this.getAt(index);
            if (task.hasDueDate()) {
               long dueDate = TaskUtilities.convertFromGMT(task.getDueDate(), tz);
               if (dueDate < time) {
                  index++;
               }
            }

            if (index < size) {
               afterCount = size - index;
            }
         }

         return afterCount;
      }
   }

   private final boolean validateObject(Object o) {
      if (!(o instanceof TaskModelImpl)) {
         EventLogger.logEvent(-1576052272418032312L, 1312904271, 2);
         return false;
      } else {
         TaskModelImpl taskModelImpl = (TaskModelImpl)o;
         if (taskModelImpl.getTaskDataModel() == null) {
            EventLogger.logEvent(-1576052272418032312L, 1129469264, 2);
            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public final synchronized void add(Object o) {
      if (this.validateObject(o)) {
         this.markDirty(true);
         super.add(o);
         Proxy.getInstance().invokeLater(new TaskCollectionImpl$MirroredCollectionAction(this, 2, this, o, null));
      }
   }

   @Override
   public final synchronized void update(Object oldObject, Object newObject) {
      if (this.validateObject(newObject)) {
         this.markDirty(true);
         super.update(oldObject, newObject);
         Proxy.getInstance().invokeLater(new TaskCollectionImpl$MirroredCollectionAction(this, 3, this, newObject, oldObject));
      }
   }

   @Override
   public final void remove(Object o) {
      this.markDirty(true);
      super.remove(o);
      Proxy.getInstance().invokeLater(new TaskCollectionImpl$MirroredCollectionAction(this, 4, this, o, null));
   }

   @Override
   public final void removeAll() {
      synchronized (this) {
         this.markDirty(true);
         super.removeAll();
      }

      this._mirroredCollection.removeAll();
   }

   @Override
   public final synchronized boolean addSyncObject(SyncObject object) {
      boolean result = true;
      if (this.validateObject(object)) {
         this.markDirty(true);
         result = super.addSyncObject(object);
      }

      return result;
   }

   @Override
   protected final synchronized void clearPersistentData() {
      this._data = null;
      this.initialize();
   }

   private TaskCollectionImpl() {
      super((Comparator)FactoryUtil.createInstance(-5646701879688313636L, null), -2395935032978087656L);
      EventLogger.register(-1576052272418032312L, "net.rim.tasks", 2);
      this._data = (SimplePersistentSyncCollection$SimpleData)super._persistentObject.getContents();
      this._mirroredCollection = new TaskCollectionImpl$MirroredTaskCollection(new TaskComparator(2));
      this.initialize();
      this.commonCtorEpilogue();
      this._currentOrder = TaskOptions.getOptions().getSortOrderIndex();
      Proxy.getInstance().invokeLater(new TaskCollectionImpl$MirroredCollectionAction(this, 1, this, null, null));
   }

   @Override
   protected final String getContentProtectionEnabledMessage() {
      return TaskResources.getString(47);
   }

   private final synchronized void initialize() {
      if (this._data == null) {
         this._data = (SimplePersistentSyncCollection$SimpleData)(new Object(16));
         super._persistentObject.setContents(this._data, 51);
         this.commit();
      }

      this.initList(this._data._items, 1);
   }

   @Override
   protected final synchronized void syncTransactionStarted(boolean limitUpdateCacheSize) {
      this.enableTaskReminders(false);
      super.syncTransactionStarted(limitUpdateCacheSize);
   }

   @Override
   protected final synchronized void syncTransactionStopped() {
      super.syncTransactionStopped();
      Task.getInstance().getUICollection().reset(this);
      this.enableTaskReminders(true);
      Proxy.getInstance().invokeLater(new TaskCollectionImpl$MirroredCollectionAction(this, 1, this, null, null));
      RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
   }

   private final void enableTaskReminders(boolean enable) {
      ReminderManager rm = ReminderManager.getInstance();
      if (rm != null) {
         rm.enableAllReminders(enable);
      }
   }

   static final TaskCollectionImpl getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      TaskCollectionImpl collection = (TaskCollectionImpl)ar.getOrWaitFor(2054667582385114214L);
      if (collection == null) {
         collection = new TaskCollectionImpl();
         ar.put(2054667582385114214L, collection);
      }

      return collection;
   }

   @Override
   public final int getSyncVersion() {
      return 2;
   }

   @Override
   public final String getSyncName() {
      return "Tasks";
   }

   @Override
   public final synchronized String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      return this._data._items.size();
   }

   @Override
   public final int size() {
      return this._data._items.size();
   }

   @Override
   public final synchronized SyncConverter getSyncConverter() {
      if (_syncConverter == null) {
         _syncConverter = (RIMModelSyncConverter)(new Object(28, -8250775496544885030L));
      }

      return _syncConverter;
   }

   @Override
   public final void endSyncCleanup() {
      super.endSyncCleanup();
      this._currentOrder = TaskOptions.getOptions().getSortOrderIndex();
      this.enableTaskReminders(true);
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      super.persistentContentModeChanged(generation);
      Proxy.getInstance().invokeLater(new TaskCollectionImpl$MirroredCollectionAction(this, 1, this, null, null));
   }
}

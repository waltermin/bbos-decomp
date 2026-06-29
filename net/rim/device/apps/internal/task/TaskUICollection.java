package net.rim.device.apps.internal.task;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.BigSortedReadableList;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskModel;

final class TaskUICollection extends BigSortedReadableList implements TaskCollection, CollectionListener {
   TaskCollection _taskCollection;
   int _statusToHide = -1;
   boolean _supressAction;
   static final int SHOW_ALL_STATUSES;

   TaskUICollection(TaskCollection taskCollection) {
      super((Comparator)FactoryUtil.createInstance(-5646701879688313636L, null));
      this._taskCollection = taskCollection;
      this._taskCollection.addCollectionListener(this);
      this.loadFromFiltered(this._taskCollection);
   }

   public final void setStatusToHide(int status) {
      boolean statusChanged = this._statusToHide != status;
      this._statusToHide = status;
      if (statusChanged) {
         this.loadFromFiltered(this._taskCollection);
      }
   }

   private final void loadFromFiltered(TaskCollection tc) {
      ReadableList list = tc;
      synchronized (list) {
         int count = list.size();
         if (count < 64) {
            count = 64;
         }

         super._elements = (BigVector)(new Object(count));

         for (int i = 0; i < list.size(); i++) {
            TaskModel tm = (TaskModel)list.getAt(i);
            if (tm.getStatus() != this._statusToHide) {
               super._elements.addElement(tm);
            }
         }

         this.sort();
      }

      this.fireReset(this);
   }

   public final int getStatusToHide() {
      return this._statusToHide;
   }

   @Override
   public final TaskCollection getCollectionStore() {
      return this._taskCollection;
   }

   @Override
   public final void add(Object element) {
      synchronized (this._taskCollection) {
         this._taskCollection.add(element);
      }
   }

   @Override
   protected final synchronized void doAdd(Object element) {
      TaskModel tm = (TaskModel)element;
      if (tm.getStatus() != this._statusToHide) {
         super.doAdd(element);
      }
   }

   @Override
   protected final synchronized boolean doUpdate(Object oldElement, Object newElement) {
      TaskModel oldTask = (TaskModel)oldElement;
      TaskModel newTask = (TaskModel)newElement;
      if (newTask.getStatus() != this._statusToHide) {
         if (oldTask.getStatus() != this._statusToHide) {
            super.doUpdate(oldElement, newElement);
            return true;
         } else {
            super.elementAdded(this, newElement);
            return true;
         }
      } else {
         if (oldTask.getStatus() != this._statusToHide) {
            super.elementRemoved(this, oldElement);
         }

         return true;
      }
   }

   @Override
   public final void update(Object oldObject, Object newObject) {
      synchronized (this._taskCollection) {
         this._taskCollection.update(oldObject, newObject);
      }
   }

   @Override
   public final void remove(Object o) {
      synchronized (this._taskCollection) {
         this._taskCollection.remove(o);
      }
   }

   @Override
   public final void removeAll() {
      synchronized (this._taskCollection) {
         this._taskCollection.removeAll();
         this.loadFromFiltered(this._taskCollection);
      }
   }

   @Override
   public final int getVersion() {
      return this._taskCollection.getVersion();
   }

   @Override
   public final boolean contains(Object element) {
      return this.getIndex(element) != -1;
   }

   @Override
   public final void reset(Collection collection) {
      this.loadFromFiltered(this._taskCollection);
   }
}

package net.rim.device.apps.internal.task;

import java.util.TimeZone;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.util.BigSortedReadableList;
import net.rim.device.api.util.Comparator;

final class TaskCollectionImpl$MirroredTaskCollection extends BigSortedReadableList {
   public TaskCollectionImpl$MirroredTaskCollection(Comparator comparator) {
   }

   public final void add(Object o) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final void update(Object oldObject, Object newObject) {
      super.doUpdate(oldObject, newObject);
   }

   public final void remove(Object o) {
      super.doRemove(o);
   }

   public final synchronized void removeAll() {
      for (int i = 0; i < this.size(); i++) {
         this.remove(this.getAt(i));
      }
   }

   final void reload(Collection c) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final synchronized int findNearestTo(long time, TimeZone tz) {
      int index = -1;
      long closestTime = Long.MAX_VALUE;

      for (int i = 0; i < this.size(); i++) {
         TaskModelImpl task = (TaskModelImpl)this.getAt(i);
         if (task.hasDueDate()) {
            long dueDate = TaskUtilities.convertFromGMT(task.getDueDate(), tz);
            long difference = Math.abs(time - dueDate);
            if (difference < closestTime) {
               closestTime = difference;
               index = i;
            }
         }
      }

      return index;
   }
}

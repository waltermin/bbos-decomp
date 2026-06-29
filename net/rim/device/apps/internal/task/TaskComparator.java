package net.rim.device.apps.internal.task;

import net.rim.device.api.util.Comparator;
import net.rim.device.internal.i18n.CollatorImpl;

final class TaskComparator implements Comparator {
   private CollatorImpl _collator = (CollatorImpl)(new Object());
   int _defaultSort = -1;
   static final int SUBJECT_SORT;
   static final int PRIORITY_SORT;
   static final int DUE_SORT;
   static final int STATUS_SORT;

   public final int secondaryCompare(int index, TaskModelImpl o1, TaskModelImpl o2) {
      if (index == 1) {
         int ret = this.primaryCompare(2, o1, o2);
         if (ret != 0) {
            return ret;
         }
      } else if (index == 2) {
         int ret = this.primaryCompare(1, o1, o2);
         if (ret != 0) {
            return ret;
         }
      }

      return index == 0 ? 0 : this._collator.compare(o1.getTitleModel(), o2.getTitleModel());
   }

   @Override
   public final int compare(Object o1, Object o2) {
      int compareType = this._defaultSort != -1 ? this._defaultSort : TaskOptions.getOptions().getSortOrderIndex();
      int ret = this.primaryCompare(compareType, (TaskModelImpl)o1, (TaskModelImpl)o2);
      return ret != 0 ? ret : this.secondaryCompare(compareType, (TaskModelImpl)o1, (TaskModelImpl)o2);
   }

   TaskComparator(int defaultSort) {
      this._defaultSort = defaultSort;
   }

   private final int primaryCompare(int index, TaskModelImpl o1, TaskModelImpl o2) {
      int ret = 0;
      switch (index) {
         case -1:
            break;
         case 0:
         default:
            ret = this._collator.compare(o1.getTitleModel(), o2.getTitleModel());
            break;
         case 1:
            ret = o1.getPriority() - o2.getPriority();
            break;
         case 2:
            ret = this.compareDueDate(o1, o2);
            break;
         case 3:
            ret = o1.getStatus() - o2.getStatus();
      }

      return ret;
   }

   TaskComparator() {
   }

   private final int compareDueDate(TaskModelImpl o1, TaskModelImpl o2) {
      long d1 = o1.getDueDate();
      long d2 = o2.getDueDate();
      if (d1 != Long.MIN_VALUE) {
         if (d2 != Long.MIN_VALUE) {
            if (d1 < d2) {
               return -1;
            } else {
               return d1 > d2 ? 1 : 0;
            }
         } else {
            return -1;
         }
      } else {
         return d2 != Long.MIN_VALUE ? 1 : 0;
      }
   }
}

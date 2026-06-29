package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.PIMItem;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;

final class TasksCollection$DueDateHandler implements LongFieldHandler {
   private TasksCollection$DueDateHandler() {
   }

   @Override
   public final long getValue(Object item) {
      if (item instanceof Object) {
         try {
            return ((PIMItem)item).getDate(103, 0);
         } finally {
            return -1;
         }
      } else {
         return -1;
      }
   }

   TasksCollection$DueDateHandler(TasksCollection$1 x0) {
      this();
   }
}

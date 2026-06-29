package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.PIMItem;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class TasksCollection$SummaryHandler implements ObjectFieldHandler {
   private TasksCollection$SummaryHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof Object) {
         try {
            return ((PIMItem)item).getString(107, 0);
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   TasksCollection$SummaryHandler(TasksCollection$1 x0) {
      this();
   }
}

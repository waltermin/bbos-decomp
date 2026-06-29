package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.ToDo;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class TasksCollection$SummaryHandler implements ObjectFieldHandler {
   private TasksCollection$SummaryHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof ToDo) {
         try {
            return ((ToDo)item).getString(107, 0);
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

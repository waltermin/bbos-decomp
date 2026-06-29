package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.ToDo;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class TasksCollection$UIDHandler implements IntFieldHandler {
   private TasksCollection$UIDHandler() {
   }

   @Override
   public final int getValue(Object item) {
      if (item instanceof ToDo) {
         try {
            String uid = ((ToDo)item).getString(108, 0);
            return Integer.parseInt(uid);
         } finally {
            return -1;
         }
      } else {
         return -1;
      }
   }

   TasksCollection$UIDHandler(TasksCollection$1 x0) {
      this();
   }
}

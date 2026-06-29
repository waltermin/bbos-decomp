package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.PIMItem;
import net.rim.wica.runtime.access.internal.data.enumeration.TaskPriorityEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class TasksCollection$PriorityHandler implements IntFieldHandler {
   private TasksCollection$PriorityHandler() {
   }

   @Override
   public final int getValue(Object item) {
      if (item instanceof Object) {
         try {
            return TaskPriorityEnumConverter.deviceToCommon(((PIMItem)item).getInt(105, 0));
         } finally {
            return -1;
         }
      } else {
         return -1;
      }
   }

   TasksCollection$PriorityHandler(TasksCollection$1 x0) {
      this();
   }
}

package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.ToDo;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class TasksCollection$NoteHandler implements ObjectFieldHandler {
   private TasksCollection$NoteHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof ToDo) {
         try {
            return ((ToDo)item).getString(104, 0);
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   TasksCollection$NoteHandler(TasksCollection$1 x0) {
      this();
   }
}

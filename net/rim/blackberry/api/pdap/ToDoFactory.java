package net.rim.blackberry.api.pdap;

import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;
import net.rim.device.api.system.CodeModuleManager;

public class ToDoFactory {
   private ToDoFactory() {
   }

   public static ToDo createToDo(int mode) {
      ToDoImpl todo = null;
      if (CodeModuleManager.getModuleHandle("net_rim_bb_task_app") != 0) {
         todo = new ToDoImpl();
         todo.initialize(mode);
      }

      return todo;
   }

   public static ToDo createToDo(int mode, Object input, ToDoList todolist) {
      ToDoImpl todo = null;
      if (CodeModuleManager.getModuleHandle("net_rim_bb_task_app") != 0) {
         todo = new ToDoImpl();
         todo.initialize(mode, input, todolist);
      }

      return todo;
   }

   public static boolean isInternalToDoModel(Object selected) {
      InternalToDo todo = (InternalToDo)createToDo(1);
      return todo == null ? false : todo.isInternalModel(selected);
   }
}

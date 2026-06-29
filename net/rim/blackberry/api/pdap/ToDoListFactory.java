package net.rim.blackberry.api.pdap;

import javax.microedition.pim.PIMException;
import javax.microedition.pim.ToDoList;
import net.rim.device.api.system.CodeModuleManager;

public class ToDoListFactory {
   private ToDoListFactory() {
   }

   public static ToDoList createToDoList(int mode) throws PIMException {
      if (CodeModuleManager.getModuleHandle("net_rim_bb_task_app") != 0) {
         return new ToDoListImpl(mode);
      } else {
         throw new PIMException();
      }
   }
}

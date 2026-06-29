package net.rim.device.apps.internal.task;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class TaskList$DeleteCompletedTasksVerb extends Verb {
   @Override
   public final String toString() {
      return TaskResources.getString(27);
   }

   TaskList$DeleteCompletedTasksVerb() {
      super(611968);
   }

   @Override
   public final Object invoke(Object context) {
      int i = 0;
      if (TaskOptions.getOptions().getConfirmDelete()) {
         int retVal = Dialog.ask(2, TaskResources.getString(38), 3);
         if (retVal != 3) {
            return null;
         }
      }

      TaskCollection realTaskCollection = TaskCollectionImpl.getInstance();

      while (i < realTaskCollection.size()) {
         TaskModelImpl tm = (TaskModelImpl)realTaskCollection.getAt(i);
         if (tm.isCompleted()) {
            realTaskCollection.remove(tm);
         } else {
            i++;
         }
      }

      return null;
   }
}

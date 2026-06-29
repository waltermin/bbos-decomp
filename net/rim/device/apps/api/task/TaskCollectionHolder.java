package net.rim.device.apps.api.task;

import net.rim.device.api.system.ApplicationRegistry;

public final class TaskCollectionHolder {
   private static final long TASKCOLLECTION_ID = 2054667582385114214L;

   private TaskCollectionHolder() {
   }

   public static final TaskCollection getTaskCollection() {
      return (TaskCollection)ApplicationRegistry.getApplicationRegistry().waitFor(2054667582385114214L);
   }
}

package net.rim.device.apps.internal.qm.peer.common;

import java.util.Hashtable;
import net.rim.device.api.system.Application;

public final class QueuesManager {
   Hashtable _queueRunners = (Hashtable)(new Object());

   public final void postInvokeLater(Application application, Runnable runnable) {
      Object obj = this._queueRunners.get(application);
      QueueRunner queue = null;
      if (!(obj instanceof QueueRunner)) {
         queue = new QueueRunner(application);
         this._queueRunners.put(application, queue);
      } else {
         queue = (QueueRunner)obj;
      }

      queue.addRunnable(runnable);
   }

   public final void schedule(Application application) {
      Object obj = this._queueRunners.get(application);
      if (obj instanceof QueueRunner) {
         ((QueueRunner)obj)._scheduled = false;
      }
   }
}

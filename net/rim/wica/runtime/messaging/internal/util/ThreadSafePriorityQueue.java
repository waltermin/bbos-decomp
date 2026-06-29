package net.rim.wica.runtime.messaging.internal.util;

import net.rim.wica.runtime.util.ThreadSafeQueue;

public class ThreadSafePriorityQueue extends ThreadSafeQueue {
   public void addPriority(Object obj) {
      synchronized (super.queue) {
         super.queue.insertElementAt(obj, 0);
         super.queue.notifyAll();
      }
   }
}

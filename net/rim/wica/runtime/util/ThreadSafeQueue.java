package net.rim.wica.runtime.util;

import java.util.Vector;

public class ThreadSafeQueue {
   protected Vector queue = (Vector)(new Object());

   public void add(Object item) {
      synchronized (this.queue) {
         this.queue.addElement(item);
         this.queue.notifyAll();
      }
   }

   public Object get() {
      synchronized (this.queue) {
         while (this.queue.size() < 1) {
            try {
               this.queue.wait();
            } finally {
               continue;
            }
         }

         Object item = this.queue.firstElement();
         this.queue.removeElementAt(0);
         return item;
      }
   }

   public boolean remove(Object msg) {
      synchronized (this.queue) {
         return this.queue.removeElement(msg);
      }
   }

   public int size() {
      synchronized (this.queue) {
         return this.queue.size();
      }
   }
}

package net.rim.wica.runtime.messaging.internal.util;

public class ConcurrentQueue$LinkedNode {
   public Object value;
   public ConcurrentQueue$LinkedNode next;

   public ConcurrentQueue$LinkedNode() {
   }

   public ConcurrentQueue$LinkedNode(Object x) {
      this.value = x;
   }

   public ConcurrentQueue$LinkedNode(Object x, ConcurrentQueue$LinkedNode n) {
      this.value = x;
      this.next = n;
   }
}

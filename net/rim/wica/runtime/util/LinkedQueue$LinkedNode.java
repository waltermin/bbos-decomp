package net.rim.wica.runtime.util;

public class LinkedQueue$LinkedNode {
   public Object value;
   public LinkedQueue$LinkedNode next;

   public LinkedQueue$LinkedNode() {
   }

   public LinkedQueue$LinkedNode(Object x) {
      this.value = x;
   }

   public LinkedQueue$LinkedNode(Object x, LinkedQueue$LinkedNode n) {
      this.value = x;
      this.next = n;
   }
}

package net.rim.wica.runtime.util;

class BoundedLinkedQueue$LinkedNode {
   public Object value;
   public BoundedLinkedQueue$LinkedNode next;

   public BoundedLinkedQueue$LinkedNode() {
   }

   public BoundedLinkedQueue$LinkedNode(Object x) {
      this.value = x;
   }

   public BoundedLinkedQueue$LinkedNode(Object x, BoundedLinkedQueue$LinkedNode n) {
      this.value = x;
      this.next = n;
   }
}

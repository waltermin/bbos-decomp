package net.rim.wica.runtime.util;

public class LinkedQueue {
   protected LinkedQueue$LinkedNode _head = new LinkedQueue$LinkedNode(null);
   protected LinkedQueue$LinkedNode _last = this._head;
   protected int _size;

   public int size() {
      return this._size;
   }

   public Object peek() {
      LinkedQueue$LinkedNode first = this._head.next;
      return first != null ? first.value : null;
   }

   public Object take() {
      Object x = null;
      LinkedQueue$LinkedNode first = this._head.next;
      if (first != null) {
         x = first.value;
         first.value = null;
         this._head = first;
         this._size--;
      }

      return x;
   }

   public void put(Object x) {
      if (x == null) {
         throw new Object();
      }

      LinkedQueue$LinkedNode p = new LinkedQueue$LinkedNode(x);
      this._last.next = p;
      this._last = p;
      this._size++;
   }

   public boolean isEmpty() {
      return this._head.next == null;
   }

   public LinkedQueue$Iterator iterator() {
      return new LinkedQueue$Iterator(this);
   }

   public void remove(Object o) {
      LinkedQueue$Iterator i = this.iterator();

      while (i.hasNext()) {
         if (o.equals(i.next())) {
            i.remove();
            break;
         }
      }
   }

   public void removeAll() {
      this._head.next = null;
      this._last = this._head;
      this._size = 0;
   }

   public void prepend(LinkedQueue queue) {
      if (queue == null) {
         throw new Object();
      }

      this._size = this._size + queue.size();
      queue.getLast().next = this._head.next;
      this._head.next = queue.getHead().next;
      queue.removeAll();
   }

   LinkedQueue$LinkedNode getHead() {
      return this._head;
   }

   LinkedQueue$LinkedNode getLast() {
      return this._last;
   }
}

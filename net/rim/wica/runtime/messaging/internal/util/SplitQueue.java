package net.rim.wica.runtime.messaging.internal.util;

import net.rim.wica.runtime.util.LinkedQueue;
import net.rim.wica.runtime.util.LinkedQueue$Iterator;
import net.rim.wica.runtime.util.LinkedQueue$LinkedNode;

public class SplitQueue extends LinkedQueue {
   private LinkedQueue$LinkedNode _split;

   @Override
   public Object take() {
      Object x = null;
      LinkedQueue$LinkedNode first = super._head.next;
      if (first != null) {
         x = first.value;
         first.value = null;
         if (this._split == super._head) {
            this._split = first;
         }

         super._head = first;
         super._size--;
      }

      return x;
   }

   public SplitQueue$SplitIterator splitIterator(boolean fromSplit) {
      return new SplitQueue$SplitIterator(this, fromSplit && this._split != null ? this._split : super._head);
   }

   @Override
   public LinkedQueue$Iterator iterator() {
      return new SplitQueue$SplitIterator(this, super._head);
   }

   @Override
   public void removeAll() {
      this._split = null;
      super._head.next = null;
      super._last = super._head;
      super._size = 0;
   }

   static int access$006(SplitQueue x0) {
      return --x0._size;
   }

   static LinkedQueue$LinkedNode access$200(SplitQueue x0) {
      return x0._last;
   }

   static LinkedQueue$LinkedNode access$302(SplitQueue x0, LinkedQueue$LinkedNode x1) {
      return x0._last = x1;
   }

   static int access$406(SplitQueue x0) {
      return --x0._size;
   }

   static LinkedQueue$LinkedNode access$500(SplitQueue x0) {
      return x0._last;
   }

   static LinkedQueue$LinkedNode access$602(SplitQueue x0, LinkedQueue$LinkedNode x1) {
      return x0._last = x1;
   }
}

package net.rim.wica.runtime.messaging.internal.util;

public class ConcurrentSplitQueue extends ConcurrentQueue {
   private ConcurrentQueue$LinkedNode _split;

   public ConcurrentSplitQueue(int initialcapacity) {
      super(initialcapacity);
   }

   public ConcurrentSplitQueue() {
   }

   @Override
   public Object take() {
      Object x = null;
      super._queueGuard.acquire();
      synchronized (super._head) {
         ConcurrentQueue$LinkedNode first = super._head.next;
         if (first != null) {
            x = first.value;
            first.value = null;
            if (this._split == super._head) {
               this._split = first;
            }

            super._head = first;
            super._takeSidePutPermits++;
         }
      }

      super._queueGuard.release();
      return x;
   }

   public ConcurrentSplitQueue$SplitIterator splitIterator(boolean fromSplit) {
      return new ConcurrentSplitQueue$SplitIterator(this, fromSplit && this._split != null ? this._split : super._head);
   }

   @Override
   public ConcurrentQueue$Iterator iterator() {
      return new ConcurrentSplitQueue$SplitIterator(this, super._head);
   }

   @Override
   public void removeAll() {
      this.lock();
      this._split = null;
      super._head.next = null;
      super._last = super._head;
      super._takeSidePutPermits = 0;
      super._putSidePutPermits = super._capacity;
      this.unlock();
   }
}

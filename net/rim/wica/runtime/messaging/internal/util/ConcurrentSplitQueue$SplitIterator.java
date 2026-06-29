package net.rim.wica.runtime.messaging.internal.util;

import java.util.NoSuchElementException;
import net.rim.device.api.system.UnsupportedOperationException;

public class ConcurrentSplitQueue$SplitIterator extends ConcurrentQueue$Iterator {
   private final ConcurrentSplitQueue this$0;

   public ConcurrentSplitQueue$SplitIterator(ConcurrentSplitQueue this$0, ConcurrentQueue$LinkedNode split) {
      super(this$0);
      this.this$0 = this$0;
      super._previous = split;
      super._current = split;
   }

   @Override
   public void remove() {
      if (Thread.currentThread() != this.this$0._owner) {
         throw new UnsupportedOperationException();
      }

      if (!super._goodState) {
         throw new IllegalStateException();
      }

      this.this$0._takeSidePutPermits++;
      if (this.this$0._split == super._current) {
         this.this$0._split = super._previous;
      }

      if (this.this$0._last == super._current) {
         this.this$0._last = super._previous;
      }

      super._previous.next = super._current.next;
      super._current = super._previous;
      super._goodState = false;
   }

   @Override
   public void removeNext() {
      if (Thread.currentThread() != this.this$0._owner) {
         throw new UnsupportedOperationException();
      }

      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      this.this$0._takeSidePutPermits++;
      if (this.this$0._split == super._current.next) {
         this.this$0._split = super._current;
      }

      if (this.this$0._last == super._current.next) {
         this.this$0._last = super._current;
      }

      super._current.next = super._current.next.next;
   }

   public void markSplit() {
      if (Thread.currentThread() != this.this$0._owner) {
         throw new UnsupportedOperationException();
      }

      this.this$0._split = super._current;
   }
}

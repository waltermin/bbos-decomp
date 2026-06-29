package net.rim.wica.runtime.messaging.internal.util;

import net.rim.wica.runtime.util.LinkedQueue$Iterator;
import net.rim.wica.runtime.util.LinkedQueue$LinkedNode;

public class SplitQueue$SplitIterator extends LinkedQueue$Iterator {
   private final SplitQueue this$0;

   public SplitQueue$SplitIterator(SplitQueue this$0, LinkedQueue$LinkedNode split) {
      super(this$0);
      this.this$0 = this$0;
      super._previous = split;
      super._current = split;
   }

   @Override
   public void remove() {
      if (!super._goodState) {
         throw new Object();
      }

      SplitQueue.access$006(this.this$0);
      if (this.this$0._split == super._current) {
         this.this$0._split = super._previous;
      }

      if (SplitQueue.access$200(this.this$0) == super._current) {
         SplitQueue.access$302(this.this$0, super._previous);
      }

      super._previous.next = super._current.next;
      super._current = super._previous;
      super._goodState = false;
   }

   @Override
   public void removeNext() {
      if (!this.hasNext()) {
         throw new Object();
      }

      SplitQueue.access$406(this.this$0);
      if (this.this$0._split == super._current.next) {
         this.this$0._split = super._current;
      }

      if (SplitQueue.access$500(this.this$0) == super._current.next) {
         SplitQueue.access$602(this.this$0, super._current);
      }

      super._current.next = super._current.next.next;
   }

   public void markSplit() {
      this.this$0._split = super._current;
   }
}

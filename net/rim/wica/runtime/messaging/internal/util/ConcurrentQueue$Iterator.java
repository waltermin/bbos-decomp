package net.rim.wica.runtime.messaging.internal.util;

import java.util.NoSuchElementException;
import net.rim.device.api.system.UnsupportedOperationException;

public class ConcurrentQueue$Iterator {
   protected ConcurrentQueue$LinkedNode _current;
   protected ConcurrentQueue$LinkedNode _previous;
   protected boolean _goodState;
   private final ConcurrentQueue this$0;

   public ConcurrentQueue$Iterator(ConcurrentQueue this$0) {
      this.this$0 = this$0;
      this._current = this$0._head;
      this._previous = this$0._head;
   }

   public boolean hasNext() {
      return this._current.next != null;
   }

   public Object next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      this._previous = this._current;
      this._current = this._current.next;
      this._goodState = true;
      return this._current.value;
   }

   public Object peekNext() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this._current.next.value;
      }
   }

   public void replace(Object x) {
      if (!this._goodState) {
         throw new IllegalStateException();
      }

      this._current.value = x;
   }

   public void remove() {
      if (Thread.currentThread() != this.this$0._owner) {
         throw new UnsupportedOperationException();
      }

      if (!this._goodState) {
         throw new IllegalStateException();
      }

      this.this$0._takeSidePutPermits++;
      if (this._current == this.this$0._last) {
         this.this$0._last = this._previous;
      }

      this._previous.next = this._current.next;
      this._current = this._previous;
      this._goodState = false;
   }

   public void removeNext() {
      if (Thread.currentThread() != this.this$0._owner) {
         throw new UnsupportedOperationException();
      }

      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      this.this$0._takeSidePutPermits++;
      if (this._current.next == this.this$0._last) {
         this.this$0._last = this._current;
      }

      this._current.next = this._current.next.next;
   }

   public void skip(int n) {
      while (n-- > 0) {
         this.next();
      }
   }
}

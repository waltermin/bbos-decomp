package net.rim.wica.runtime.util;

public class LinkedQueue$Iterator {
   protected LinkedQueue$LinkedNode _current;
   protected LinkedQueue$LinkedNode _previous;
   protected boolean _goodState;
   private final LinkedQueue this$0;

   public LinkedQueue$Iterator(LinkedQueue this$0) {
      this.this$0 = this$0;
      this._current = this$0._head;
      this._previous = this$0._head;
   }

   public boolean hasNext() {
      return this._current.next != null;
   }

   public Object next() {
      if (!this.hasNext()) {
         throw new Object();
      }

      this._previous = this._current;
      this._current = this._current.next;
      this._goodState = true;
      return this._current.value;
   }

   public Object peekNext() {
      if (!this.hasNext()) {
         throw new Object();
      } else {
         return this._current.next.value;
      }
   }

   public void replace(Object x) {
      if (!this._goodState) {
         throw new Object();
      }

      this._current.value = x;
   }

   public void remove() {
      if (!this._goodState) {
         throw new Object();
      }

      if (this._current == this.this$0._last) {
         this.this$0._last = this._previous;
      }

      this._previous.next = this._current.next;
      this._current = this._previous;
      this._goodState = false;
      this.this$0._size--;
   }

   public void removeNext() {
      if (!this.hasNext()) {
         throw new Object();
      }

      if (this._current.next == this.this$0._last) {
         this.this$0._last = this._current;
      }

      this._current.next = this._current.next.next;
      this.this$0._size--;
   }

   public void skip(int n) {
      while (n-- > 0) {
         this.next();
      }
   }
}

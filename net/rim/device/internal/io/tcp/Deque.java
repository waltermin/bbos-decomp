package net.rim.device.internal.io.tcp;

public final class Deque {
   private Deque$Element _head;
   private Deque$Element _tail;
   private int _size;

   public final void add(Object obj) {
      this.enqueueTail(obj);
   }

   public final Object dequeueHead() {
      return this.dequeueHeadElement()._data;
   }

   private final Deque$Element dequeueHeadElement() {
      if (this._size == 0) {
         throw new Object();
      } else {
         Deque$Element out = this._head;
         if (this._size == 1) {
            this._tail = null;
            this._head = null;
            this._size = 0;
            return out;
         } else {
            this._head = this._head._next;
            this._head._prev = null;
            this._size--;
            return out;
         }
      }
   }

   public final Object dequeueTail() {
      if (this._size == 0) {
         throw new Object();
      } else {
         return this._size == 1 ? this.dequeueHead() : this.dequeueTailElement()._data;
      }
   }

   private final Deque$Element dequeueTailElement() {
      if (this._size == 0) {
         throw new Object();
      }

      if (this._size == 1) {
         return this.dequeueHeadElement();
      }

      Deque$Element out = this._tail;
      this._tail = this._tail._prev;
      this._tail._next = null;
      this._size--;
      return out;
   }

   public final void enqueueHead(Object obj) {
      this.enqueueHead(new Deque$Element(obj, null));
   }

   private final void enqueueHead(Deque$Element newElement) {
      if (this._size > 0) {
         newElement._next = this._head;
         this._head._prev = newElement;
         this._head = newElement;
      } else {
         this._head = newElement;
         this._head._next = null;
         this._head._prev = null;
         this._tail = this._head;
      }

      this._size++;
   }

   public final void enqueueTail(Object obj) {
      this.enqueueTail(new Deque$Element(obj, null));
   }

   private final void enqueueTail(Deque$Element newElement) {
      if (this._size > 0) {
         newElement._next = null;
         this._tail._next = newElement;
         newElement._prev = this._tail;
         this._tail = newElement;
         this._size++;
      } else {
         this.enqueueHead(newElement);
      }
   }

   public final Object get(int index) {
      return this.getElement(index)._data;
   }

   private final Deque$Element getElement(int index) {
      if (this._size == 0) {
         throw new Object();
      }

      if (index <= this._size - 1 && index >= 0) {
         Deque$Element temp;
         if (index >= this._size >> 1) {
            temp = this._tail;

            for (int i = this._size - 1; i > index; i--) {
               temp = temp._prev;
            }
         } else {
            temp = this._head;

            for (int i = 0; i < index; i++) {
               temp = temp._next;
            }
         }

         return temp;
      } else {
         throw new Object();
      }
   }

   public final Object getHead() {
      return this._head._data;
   }

   public final int getSize() {
      return this._size;
   }

   public final Object getTail() {
      return this._tail._data;
   }

   public final void insertAt(Object obj, int index) {
      this.insertElementAt(new Deque$Element(obj, null), index);
   }

   private final void insertElementAt(Deque$Element newElement, int index) {
      if (index > this._size || index < 0) {
         throw new Object();
      }

      if (this._size == index) {
         this.enqueueTail(newElement);
      } else if (index == 0) {
         this.enqueueHead(newElement);
      } else {
         Deque$Element temp = this.getElement(index);
         newElement._prev = temp._prev;
         newElement._next = temp;
         temp._prev._next = newElement;
         temp._prev = newElement;
         this._size++;
      }
   }

   public final void purge() {
      this._head = null;
      this._tail = null;
      this._size = 0;
   }

   public final Object remove(int index) {
      return this.removeElement(index)._data;
   }

   private final Deque$Element removeElement(int index) {
      if (index == 0) {
         return this.dequeueHeadElement();
      }

      if (index == this._size - 1) {
         return this.dequeueTailElement();
      }

      Deque$Element temp = this.getElement(index);
      temp._next._prev = temp._prev;
      temp._prev._next = temp._next;
      this._size--;
      return temp;
   }
}

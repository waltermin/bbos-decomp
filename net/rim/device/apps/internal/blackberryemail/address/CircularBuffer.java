package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.vm.Persistable;

public class CircularBuffer implements Persistable {
   private int[] _buffer = null;
   private int _bufferLength;
   private int _marker;
   private int _head;
   private int _tail;
   private int _cursor;
   private int _next;

   static void test() {
      CircularBuffer cb = new CircularBuffer(10);
      int[] l1 = new int[]{1, 2, 3, 4, -804651006, 5, 6, -804651007, 7, -805044223, 32, -804651004, 300, 300, 1200, 1800};
      int[] l2 = new int[]{5, 6, -804651007, 7, -805044223, 32, -804651004, 300};
      int[] l3 = new int[]{7, -805044223, 32, -804651004};
      cb.addListToTail(l1);
      cb.addListToTail(l2);
      cb.addListToTail(l3);
      cb.addListToTail(l1);

      for (int length = cb.firstEntry(); cb.hasNext(); length = cb.nextEntry()) {
         for (int i = 0; i < length; i++) {
            int s = cb.getElement(i);
            System.out.println(s);
         }
      }
   }

   public CircularBuffer(int length) {
      this.initialize(length, -1);
   }

   public CircularBuffer(int length, int marker) {
      this.initialize(length, marker);
   }

   private void initialize(int length, int marker) {
      this._marker = marker;
      this._bufferLength = length;
      this._buffer = new int[length];
      this._head = 0;
      this._tail = 0;
      this._cursor = -1;
      this._next = -1;
   }

   public void addList(int[] list) {
      int listLength = list.length;
      if (listLength + 1 > this._bufferLength - 1) {
         this._assert(false);
      } else {
         while (!this.hasRoomFor(listLength)) {
            this.deleteFromTail();
         }

         this.addElement(this._marker);

         for (int i = listLength - 1; i >= 0; i--) {
            this._assert(list[i] != this._marker);
            this.addElement(list[i]);
         }

         this.resetCursor();
      }
   }

   public void addListToTail(int[] list) {
      int listLength = list.length;
      if (listLength + 1 > this._bufferLength - 1) {
         this._assert(false);
      } else {
         while (!this.hasRoomFor(listLength)) {
            this.deleteFromHead();
         }

         for (int i = 0; i < listLength; i++) {
            this._assert(list[i] != this._marker);
            this.addElementToTail(list[i]);
         }

         this.addElementToTail(this._marker);
         this.resetCursor();
      }
   }

   public int firstEntry() {
      if (this.isEmpty()) {
         return -1;
      }

      this._cursor = this._head;
      int length = this.findNext();
      this._assert(this.isOnMarker(this._next));
      return length;
   }

   public int nextEntry() {
      if (!this.hasNext()) {
         this._assert(false);
         return -1;
      } else {
         this._cursor = this._next;
         this._assert(this.isOnMarker(this._cursor));
         int length = this.findNext();
         this._assert(this.isOnMarker(this._next));
         return length;
      }
   }

   public boolean hasNext() {
      return !this.isEmpty() && this._cursor != this._tail;
   }

   public int getElement(int index) {
      this._assert(this._cursor >= 0 && this._cursor < this._bufferLength);
      int datum = this._buffer[this.plus(this._cursor, -index - 1)];
      this._assert(datum != this._marker);
      return datum;
   }

   public void makeEmpty() {
      this._head = this._tail = 0;
      this.resetCursor();
   }

   public boolean isEmpty() {
      return this._head == this._tail;
   }

   public boolean isFull() {
      return this.succ(this._head) == this._tail;
   }

   public int freeSpace() {
      int allocatedSpace = (this._head + this._bufferLength - this._tail) % this._bufferLength;
      return this._bufferLength - allocatedSpace;
   }

   public boolean hasRoomFor(int listLength) {
      return listLength + 1 < this.freeSpace();
   }

   public void deleteFromTail() {
      if (this.isEmpty()) {
         this._assert(false);
      } else {
         this._tail = this.succ(this._tail);

         while (!this.isEmpty() && !this.isOnMarker(this._tail)) {
            this._tail = this.succ(this._tail);
         }
      }
   }

   public void deleteFromHead() {
      if (this.isEmpty()) {
         this._assert(false);
      } else {
         this._head = this.pred(this._head);

         while (!this.isEmpty() && !this.isOnMarker(this._head)) {
            this._head = this.pred(this._head);
         }
      }
   }

   private int findNext() {
      this._assert(this.validIndex(this._cursor));
      this._next = this.pred(this._cursor);

      int length;
      for (length = 0; !this.isOnMarker(this._next); length++) {
         this._next = this.pred(this._next);
      }

      return length;
   }

   private void addElement(int element) {
      if (this.isFull()) {
         this._assert(false);
      } else {
         this._assert(this.validIndex(this._head));
         this._buffer[this._head] = element;
         this._head = this.succ(this._head);
      }
   }

   private void addElementToTail(int element) {
      if (this.isFull()) {
         this._assert(false);
      } else {
         this._tail = this.pred(this._tail);
         this._assert(this.validIndex(this._tail));
         this._buffer[this._tail] = element;
      }
   }

   private void resetCursor() {
      this.firstEntry();
   }

   private int pred(int pos) {
      this._assert(this.validIndex(pos));
      int p = (pos - 1 + this._bufferLength) % this._bufferLength;
      this._assert(this.validIndex(p));
      return p;
   }

   private int succ(int pos) {
      this._assert(this.validIndex(pos));
      int s = (pos + 1) % this._bufferLength;
      this._assert(this.validIndex(s));
      return s;
   }

   private int plus(int pos, int offset) {
      this._assert(this.validIndex(pos));
      int p = (pos + offset + this._bufferLength) % this._bufferLength;
      this._assert(this.validIndex(p));
      return p;
   }

   private boolean validIndex(int index) {
      return index >= 0 && index < this._bufferLength;
   }

   private boolean isOnMarker(int index) {
      this._assert(this.validIndex(index));
      return this._buffer[index] == this._marker;
   }

   protected void _assert(boolean condition) {
      if (!condition) {
         System.out.println("Circular buffer assert failure!");
      }
   }
}

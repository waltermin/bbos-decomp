package net.rim.device.api.util;

import net.rim.vm.Array;

public final class CyclicQueue {
   private int _initialCapacity;
   private Object[] _queue;
   private int _capacity;
   private int _modulus;
   private int _head;
   private int _tail;
   public static final int DEFAULT_INITIAL_CAPACITY = 8;

   public CyclicQueue() {
      this(8);
   }

   public CyclicQueue(int initialCapacity) {
      initialCapacity = roundCapacity(initialCapacity);
      this._initialCapacity = initialCapacity;
      this._queue = new Object[initialCapacity];
      this._capacity = initialCapacity;
      this._modulus = initialCapacity - 1;
   }

   public final void enqueue(Object object) {
      this._queue[this._head] = object;
      this._head = this._head + 1 & this._modulus;
      if (this._head == this._tail) {
         this.expandCapacity();
      }
   }

   public final Object dequeue() {
      if (this._head != this._tail) {
         Object object = this._queue[this._tail];
         this._queue[this._tail] = null;
         this._tail = this._tail + 1 & this._modulus;
         if (this._head == this._tail && this._capacity > this._initialCapacity) {
            Array.resize(this._queue, this._initialCapacity);
            this._capacity = this._initialCapacity;
            this._modulus = this._initialCapacity - 1;
            this._head = this._tail = 0;
         }

         return object;
      } else {
         return null;
      }
   }

   public final void flush() {
      this._queue = new Object[this._initialCapacity];
      this._capacity = this._initialCapacity;
      this._modulus = this._initialCapacity - 1;
      this._head = this._tail = 0;
   }

   public final boolean isEmpty() {
      return this._head == this._tail;
   }

   public final int size() {
      return this._head - this._tail & this._modulus;
   }

   private static final int roundCapacity(int capacity) {
      if (capacity <= 8) {
         return 8;
      }

      capacity--;

      int ret;
      for (ret = 8; capacity > 7; ret <<= 1) {
         capacity >>>= 1;
      }

      return ret;
   }

   private final void expandCapacity() {
      int newCapacity = this._capacity << 1;
      Array.resize(this._queue, newCapacity);
      if (this._head <= this._capacity >> 1) {
         this._head = this._head + this._capacity;
         if (this._tail > 0) {
            System.arraycopy(this._queue, 0, this._queue, this._capacity, this._tail);

            for (int i = 0; i < this._tail; i++) {
               this._queue[i] = null;
            }
         }
      } else {
         this._tail = this._tail + this._capacity;
         System.arraycopy(this._queue, this._head, this._queue, this._tail, this._capacity - this._head);

         for (int i = this._head; i < this._capacity; i++) {
            this._queue[i] = null;
         }
      }

      this._capacity = newCapacity;
      this._modulus = newCapacity - 1;
   }
}

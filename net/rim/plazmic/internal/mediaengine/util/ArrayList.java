package net.rim.plazmic.internal.mediaengine.util;

public class ArrayList {
   private Array _array;
   private int _size;
   private int _increment;
   public static final int DEFAULT_CAPACITY = 10;
   public static final int DEFAULT_CAPACITY_INCREMENT = 10;

   public ArrayList(Array array) {
      this(array, 10, 10);
   }

   public ArrayList(Array array, int capacity, int capacityIncrement) {
      if (array != null && capacity >= 0 && capacityIncrement > 0) {
         this._array = array;
         this._array.init(capacity);
         this._increment = capacityIncrement;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void add(Object o) {
      this.ensureCapacity(this._size + 1);
      this._array.set(o, this._size++);
   }

   public int size() {
      return this._size;
   }

   public int capacity() {
      return this._array.length();
   }

   public void ensureCapacity(int capacity) {
      int currentCapacity = this.capacity();
      if (capacity > currentCapacity) {
         this._array.growTo(Math.max(capacity, currentCapacity + this._increment), 0, this._size, false);
      }
   }

   public void get(Object o, int index) {
      if (index < this._size && index >= 0) {
         this._array.get(o, index);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void set(Object o, int index) {
      if (index < this._size && index >= 0) {
         this._array.set(o, index);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clear() {
      this._size = 0;
   }

   public int indexOf(Object o) {
      int index = -1;

      for (int i = 0; i < this._size; i++) {
         if (this._array.equals(i, o)) {
            return i;
         }
      }

      return index;
   }

   public void remove(Object o) {
      int index = this.indexOf(o);
      if (index != -1) {
         this._size--;
         if (index != this._size) {
            this._array.copy(index + 1, index, this._size - index);
         }
      }
   }
}

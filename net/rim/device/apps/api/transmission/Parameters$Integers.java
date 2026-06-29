package net.rim.device.apps.api.transmission;

import net.rim.vm.Array;

final class Parameters$Integers {
   private int _increment;
   private int[] _data;
   private int _size;

   private Parameters$Integers(int capacityInt, int incrementInt) {
      this._increment = incrementInt;
      this.ensureCapacity(capacityInt);
      this._size = 0;
   }

   private final void ensureCapacity(int capacityInt) {
      int capacityToUse = capacityInt < this._increment ? this._increment : capacityInt;
      if (this._data == null) {
         this._data = new int[capacityToUse];
      } else {
         if (this._data.length - this._size < capacityToUse) {
            Array.resize(this._data, this._data.length + capacityToUse);
         }
      }
   }

   private final void add(int anInt) {
      this.insert(this._size, anInt);
   }

   private final void insert(int indexInt, int anInt) {
      this.ensureCapacity(1);
      if (indexInt < this._size) {
         System.arraycopy(this._data, indexInt, this._data, indexInt + 1, this._size - indexInt);
      }

      this._data[indexInt] = anInt;
      this._size++;
   }

   private final int get(int indexInt) {
      return this._data[indexInt];
   }

   private final void removeAll() {
      this._size = 0;
   }

   private final int size() {
      return this._size;
   }

   Parameters$Integers(int x0, int x1, Parameters$1 x2) {
      this(x0, x1);
   }
}

package net.rim.device.api.util;

import java.util.Enumeration;
import net.rim.vm.Array;

public class IntMultiMap implements Persistable {
   private int[] _ints;
   private Object[] _objects;
   private int _num;
   private boolean _sortRequired;
   private boolean _allowDuplicates;

   public IntMultiMap(int initialCapacity, boolean allowDuplicates) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException();
      }

      this._ints = new int[initialCapacity + 1];
      this._objects = new Object[initialCapacity + 1];
      this._allowDuplicates = allowDuplicates;
   }

   public IntMultiMap() {
      this(16, false);
   }

   public boolean isEmpty() {
      return this._num == 0;
   }

   public void clear() {
      this._num = 0;
      this._sortRequired = false;
   }

   public void trim() {
      this.verifySorted();
      Array.resize(this._ints, this._num + 1);
      Array.resize(this._objects, this._num + 1);
   }

   public void add(int key, Object value) {
      if (this._num == this._ints.length - 1) {
         int size = this._ints.length * 2;
         Array.resize(this._ints, size);
         Array.resize(this._objects, size);
      }

      this._ints[this._num] = key;
      this._objects[this._num++] = value;
      this._sortRequired = true;
   }

   protected void verifySorted() {
      if (this._sortRequired) {
         this._sortRequired = false;
         Arrays.sort(this._ints, 0, this._num, this._objects);
         this._ints[this._num] = this._ints[this._num - 1] + 1;
         if (!this._allowDuplicates) {
            for (int i = 0; i < this._num; i++) {
               int j = i + 1;

               while (j < this._num && this._ints[i] == this._ints[j]) {
                  if (this._objects[j].equals(this._objects[i])) {
                     int copyLength = this._num - j;
                     System.arraycopy(this._ints, j + 1, this._ints, j, copyLength);
                     System.arraycopy(this._objects, j + 1, this._objects, j, copyLength);
                     this._num--;
                  } else {
                     j++;
                  }
               }
            }
         }
      }
   }

   protected int findKey(int key) {
      this.verifySorted();
      int i = Arrays.binarySearch(this._ints, key, 0, this._num);

      while (i > 0 && this._ints[i - 1] == key) {
         i--;
      }

      return i;
   }

   public boolean removeKey(int key) {
      int start = this.findKey(key);
      if (start < 0) {
         return false;
      }

      int end = start;

      while (this._ints[++end] == key) {
      }

      int copyLength = this._num - end + 1;
      System.arraycopy(this._ints, end, this._ints, start, copyLength);
      System.arraycopy(this._objects, end, this._objects, start, copyLength);
      this._num -= end - start;
      return true;
   }

   public boolean removeValue(int key, Object value) {
      int i = this.findKey(key);
      if (i < 0) {
         return false;
      }

      do {
         if (this._objects[i].equals(value)) {
            int iPlus1 = i + 1;
            int copy = this._num - iPlus1 + 1;
            System.arraycopy(this._ints, iPlus1, this._ints, i, copy);
            System.arraycopy(this._objects, iPlus1, this._objects, i, copy);
            this._num--;
         } else {
            i++;
         }
      } while (this._ints[i] == key);

      return true;
   }

   public boolean removeValue(Object value) {
      int num = this._num;

      for (int i = this._num - 1; i >= 0; i--) {
         if (this._objects[i].equals(value)) {
            int iPlus1 = i + 1;
            int copy = this._num - iPlus1 + 1;
            System.arraycopy(this._ints, iPlus1, this._ints, i, copy);
            System.arraycopy(this._objects, iPlus1, this._objects, i, copy);
            this._num--;
         }
      }

      this._sortRequired = this._sortRequired & this._num > 1;
      return this._num != num;
   }

   public boolean containsKey(int key) {
      return this.findKey(key) >= 0;
   }

   public boolean containsValue(int key, Object value) {
      int i = this.findKey(key);
      if (i < 0) {
         return false;
      }

      while (!this._objects[i].equals(value)) {
         if (this._ints[++i] != key) {
            return false;
         }
      }

      return true;
   }

   public IntEnumeration keys() {
      this.verifySorted();
      return new IntMultiMap$KeysEnumeration(this);
   }

   public Enumeration elements(int key) {
      this.verifySorted();
      return new IntMultiMap$KeyValuesEnumeration(this, key);
   }

   public Enumeration elements() {
      this.verifySorted();
      return new IntMultiMap$ValuesEnumeration(this);
   }

   public int size() {
      if (this._num == 0) {
         return 0;
      }

      this.verifySorted();
      int size = 1;
      int key = this._ints[0];

      for (int i = 0; i < this._num; i++) {
         if (this._ints[i] != key) {
            key = this._ints[i];
            size++;
         }
      }

      return size;
   }

   public int size(int key) {
      int i = this.findKey(key);
      if (i < 0) {
         return 0;
      }

      int size = 1;

      while (this._ints[++i] == key) {
         size++;
      }

      return size;
   }
}

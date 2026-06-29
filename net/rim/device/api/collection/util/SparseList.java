package net.rim.device.api.collection.util;

import java.util.Enumeration;
import net.rim.device.api.collection.ReadableIntMap;
import net.rim.device.api.collection.WritableList;
import net.rim.device.api.util.ObjectEnumerator;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class SparseList implements Persistable, WritableList, ReadableIntMap {
   private int _chunkSize;
   private Object[] _objects;
   private int _objectCount;
   private int[] _holes;
   private int _holeCount;

   public Enumeration elements() {
      return new ObjectEnumerator(this._objects);
   }

   public int addAndGetIndex(Object element) {
      int insertLocation = -1;
      if (element == null) {
         throw new NullPointerException();
      }

      if (this._holeCount > 0) {
         this._holeCount--;
         insertLocation = this._holes[this._holeCount];
      } else if (this._objectCount < this._objects.length) {
         insertLocation = this._objectCount;
      } else {
         Array.resize(this._objects, this._objects.length + this._chunkSize);
         insertLocation = this._objectCount;
      }

      this._objects[insertLocation] = element;
      this._objectCount++;
      return insertLocation;
   }

   @Override
   public void insertAt(int index, Object element) {
      if (element == null) {
         throw new NullPointerException();
      }

      this.get(index);
      this._objects[index] = element;
   }

   @Override
   public void removeAt(int index) {
      if (index >= 0 && index < this._objects.length && this._objects[index] != null) {
         this._objects[index] = null;
         this._objectCount--;
         if (index < this._objectCount + this._holeCount) {
            if (this._holeCount == this._holes.length) {
               Array.resize(this._holes, this._holeCount * 2);
            }

            this._holes[this._holeCount] = index;
            this._holeCount++;
         } else {
            this.removeUnneededHoles();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   @Override
   public void remove(Object element) {
      for (int i = this._objects.length - 1; i >= 0; i--) {
         if (this._objects[i] == element) {
            this.removeAt(i);
            return;
         }
      }
   }

   @Override
   public void removeAll() {
      this.initialize(0);
   }

   @Override
   public int size() {
      return this._objectCount;
   }

   @Override
   public Object get(int index) {
      return index >= 0 && index < this._objects.length && this._objects[index] != null ? this._objects[index] : null;
   }

   @Override
   public int getKey(Object element) {
      for (int i = 0; i < this._objects.length; i++) {
         if (this._objects[i] == element) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public boolean contains(int index) {
      return index >= 0 && index < this._objects.length && this._objects[index] != null;
   }

   @Override
   public void add(Object element) {
      this.addAndGetIndex(element);
   }

   public SparseList() {
      this(0);
   }

   private void initialize(int initialCapacity) {
      if (initialCapacity <= 0) {
         initialCapacity = 0;
      }

      this._objects = new Object[0];
      this._holes = new int[4];
      this._objectCount = 0;
      this._holeCount = 0;
      this._chunkSize = Array.getSectionSize(this._objects) / 4;
      Array.resize(this._objects, (initialCapacity / this._chunkSize + 1) * this._chunkSize);
   }

   private synchronized void removeUnneededHoles() {
      int i = 0;

      while (i < this._holeCount) {
         if (this._holes[i] >= this._objectCount + this._holeCount) {
            this._holeCount--;
            this._holes[i] = this._holes[this._holeCount];
            i = 0;
         } else {
            i++;
         }
      }

      int newSize = this._holeCount * 4 / 3 + 4;
      if (newSize < this._holes.length) {
         Array.resize(this._holes, newSize);
      }
   }

   public SparseList(int n) {
      this.initialize(n);
   }
}

package net.rim.wica.runtime.util;

import java.util.NoSuchElementException;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class DoubleVector implements Persistable {
   protected double[] _elementData;
   protected int _elementCount;
   protected int _capacityIncrement;

   public DoubleVector(int initialCapacity, int capacityIncrement) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      }

      this._elementData = new double[initialCapacity];
      this._capacityIncrement = capacityIncrement;
   }

   public DoubleVector(int initialCapacity) {
      this(initialCapacity, 0);
   }

   public DoubleVector() {
      this(10);
   }

   public void copyInto(double[] anArray) {
      System.arraycopy(this._elementData, 0, anArray, 0, this._elementCount);
   }

   public double[] toArray() {
      double[] newArray = new double[this._elementCount];
      System.arraycopy(this._elementData, 0, newArray, 0, this._elementCount);
      return newArray;
   }

   public double[] getArray() {
      return this._elementData;
   }

   public void trimToSize() {
      if (this._elementCount < this._elementData.length) {
         Array.resize(this._elementData, this._elementCount);
      }
   }

   public void ensureCapacity(int minCapacity) {
      if (minCapacity > this._elementData.length) {
         this.ensureCapacityHelper(minCapacity);
      }
   }

   private void ensureCapacityHelper(int minCapacity) {
      int oldCapacity = this._elementData.length;
      int newCapacity = this._capacityIncrement > 0 ? oldCapacity + this._capacityIncrement : oldCapacity * 2;
      if (newCapacity < minCapacity) {
         newCapacity = minCapacity;
      }

      Array.resize(this._elementData, newCapacity);
   }

   public void setSize(int newSize) {
      if (newSize > this._elementCount && newSize > this._elementData.length) {
         this.ensureCapacityHelper(newSize);
      }

      this._elementCount = newSize;
   }

   public int capacity() {
      return this._elementData.length;
   }

   public int size() {
      return this._elementCount;
   }

   public boolean isEmpty() {
      return this._elementCount == 0;
   }

   public boolean contains(double elem) {
      return this.indexOf(elem, 0) >= 0;
   }

   public int indexOf(double elem) {
      return this.indexOf(elem, 0);
   }

   public int indexOf(double elem, int index) {
      for (int i = index; i < this._elementCount; i++) {
         if (elem == this._elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(double elem) {
      return this.lastIndexOf(elem, this._elementCount - 1);
   }

   public int lastIndexOf(double elem, int index) {
      if (index >= this._elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._elementCount);
      }

      for (int i = index; i >= 0; i--) {
         if (elem == this._elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public double elementAt(int index) {
      if (index >= this._elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._elementCount);
      }

      boolean var4 = false /* VF: Semaphore variable */;

      double var10000;
      try {
         var4 = true;
         var10000 = this._elementData[index];
         var4 = false;
      } finally {
         if (var4) {
            throw new ArrayIndexOutOfBoundsException(index + " < 0");
         }
      }

      return var10000;
   }

   public double firstElement() {
      if (this._elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this._elementData[0];
      }
   }

   public double lastElement() {
      if (this._elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this._elementData[this._elementCount - 1];
      }
   }

   public void setElementAt(double obj, int index) {
      if (index >= this._elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._elementCount);
      }

      this._elementData[index] = obj;
   }

   public void removeElementAt(int index) {
      if (index >= this._elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this._elementCount);
      }

      if (index < 0) {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      int j = this._elementCount - index - 1;
      if (j > 0) {
         System.arraycopy(this._elementData, index + 1, this._elementData, index, j);
      }

      this._elementCount--;
   }

   public void insertElementAt(double obj, int index) {
      int newcount = this._elementCount + 1;
      if (index >= newcount) {
         throw new ArrayIndexOutOfBoundsException(index + " > " + this._elementCount);
      }

      if (newcount > this._elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      System.arraycopy(this._elementData, index, this._elementData, index + 1, this._elementCount - index);
      this._elementData[index] = obj;
      this._elementCount++;
   }

   public void addElement(double obj) {
      int newcount = this._elementCount + 1;
      if (newcount > this._elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      this._elementData[this._elementCount++] = obj;
   }

   public boolean removeElement(double obj) {
      int i = this.indexOf(obj);
      if (i >= 0) {
         this.removeElementAt(i);
         return true;
      } else {
         return false;
      }
   }

   public void removeAllElements() {
      this._elementCount = 0;
   }
}

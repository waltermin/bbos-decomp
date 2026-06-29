package net.rim.device.api.util;

import java.util.NoSuchElementException;
import net.rim.vm.Array;

public class IntVector implements Persistable {
   protected int[] elementData;
   protected int elementCount;
   protected int capacityIncrement;

   public IntVector(int initialCapacity, int capacityIncrement) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      }

      this.elementData = new int[initialCapacity];
      this.capacityIncrement = capacityIncrement;
   }

   public IntVector(int initialCapacity) {
      this(initialCapacity, 0);
   }

   public IntVector() {
      this(10);
   }

   public void copyInto(int[] anArray) {
      int i = this.elementCount;

      while (i-- > 0) {
         anArray[i] = this.elementData[i];
      }
   }

   public int[] toArray() {
      int[] newArray = new int[this.elementCount];
      System.arraycopy(this.elementData, 0, newArray, 0, this.elementCount);
      return newArray;
   }

   public int[] getArray() {
      return this.elementData;
   }

   public void trimToSize() {
      if (this.elementCount < this.elementData.length) {
         Array.resize(this.elementData, this.elementCount);
      }
   }

   public void ensureCapacity(int minCapacity) {
      if (minCapacity > this.elementData.length) {
         this.ensureCapacityHelper(minCapacity);
      }
   }

   private void ensureCapacityHelper(int minCapacity) {
      int oldCapacity = this.elementData.length;
      int newCapacity = this.capacityIncrement > 0 ? oldCapacity + this.capacityIncrement : oldCapacity * 2;
      if (newCapacity < minCapacity) {
         newCapacity = minCapacity;
      }

      Array.resize(this.elementData, newCapacity);
   }

   public void setSize(int newSize) {
      if (newSize > this.elementCount && newSize > this.elementData.length) {
         this.ensureCapacityHelper(newSize);
      }

      this.elementCount = newSize;
   }

   public int capacity() {
      return this.elementData.length;
   }

   public int size() {
      return this.elementCount;
   }

   public boolean isEmpty() {
      return this.elementCount == 0;
   }

   public boolean contains(int elem) {
      return this.indexOf(elem, 0) >= 0;
   }

   public int indexOf(int elem) {
      return this.indexOf(elem, 0);
   }

   public int indexOf(int elem, int index) {
      for (int i = index; i < this.elementCount; i++) {
         if (elem == this.elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(int elem) {
      return this.lastIndexOf(elem, this.elementCount - 1);
   }

   public int lastIndexOf(int elem, int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      }

      for (int i = index; i >= 0; i--) {
         if (elem == this.elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public int elementAt(int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      }

      try {
         return this.elementData[index];
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException(index + " < 0");
      }
   }

   public int firstElement() {
      if (this.elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this.elementData[0];
      }
   }

   public int lastElement() {
      if (this.elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this.elementData[this.elementCount - 1];
      }
   }

   public void setElementAt(int obj, int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      }

      this.elementData[index] = obj;
   }

   public void removeElementAt(int index) {
      if (index >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + this.elementCount);
      }

      if (index < 0) {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      int j = this.elementCount - index - 1;
      if (j > 0) {
         System.arraycopy(this.elementData, index + 1, this.elementData, index, j);
      }

      this.elementCount--;
   }

   public void insertElementAt(int obj, int index) {
      int newcount = this.elementCount + 1;
      if (index >= newcount) {
         throw new ArrayIndexOutOfBoundsException(index + " > " + this.elementCount);
      }

      if (newcount > this.elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      System.arraycopy(this.elementData, index, this.elementData, index + 1, this.elementCount - index);
      this.elementData[index] = obj;
      this.elementCount++;
   }

   public void addElement(int obj) {
      int newcount = this.elementCount + 1;
      if (newcount > this.elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      this.elementData[this.elementCount++] = obj;
   }

   public boolean removeElement(int obj) {
      int i = this.indexOf(obj);
      if (i >= 0) {
         this.removeElementAt(i);
         return true;
      } else {
         return false;
      }
   }

   public void removeAllElements() {
      this.elementCount = 0;
   }
}

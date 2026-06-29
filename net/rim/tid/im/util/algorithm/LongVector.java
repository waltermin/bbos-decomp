package net.rim.tid.im.util.algorithm;

import net.rim.vm.Array;
import net.rim.vm.Persistable;

public class LongVector implements Persistable {
   protected long[] elementData;
   protected int elementCount;
   protected int capacityIncrement;

   public LongVector(int initialCapacity, int capacityIncrement) {
      if (initialCapacity < 0) {
         throw new Object(((StringBuffer)(new Object("Illegal Capacity: "))).append(initialCapacity).toString());
      }

      this.elementData = new long[initialCapacity];
      this.capacityIncrement = capacityIncrement;
   }

   public LongVector(int initialCapacity) {
      this(initialCapacity, 0);
   }

   public LongVector() {
      this(10);
   }

   public void copyInto(int offset, int len, long[] array, int dstPosition) {
      System.arraycopy(this.elementData, offset, array, dstPosition, len);
   }

   public long[] toArray() {
      long[] newArray = new long[this.elementCount];
      System.arraycopy(this.elementData, 0, newArray, 0, this.elementCount);
      return newArray;
   }

   public long[] getArray() {
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

   public boolean contains(long elem) {
      return this.indexOf(elem, 0) >= 0;
   }

   public int indexOf(long elem) {
      return this.indexOf(elem, 0);
   }

   public int indexOf(long elem, int index) {
      for (int i = index; i < this.elementCount; i++) {
         if (elem == this.elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(long elem) {
      return this.lastIndexOf(elem, this.elementCount - 1);
   }

   public int lastIndexOf(long elem, int index) {
      if (index >= this.elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this.elementCount).toString());
      }

      for (int i = index; i >= 0; i--) {
         if (elem == this.elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public long elementAt(int index) {
      if (index >= this.elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this.elementCount).toString());
      }

      try {
         return this.elementData[index];
      } finally {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" < 0").toString());
      }
   }

   public long firstElement() {
      if (this.elementCount == 0) {
         throw new Object();
      } else {
         return this.elementData[0];
      }
   }

   public long lastElement() {
      if (this.elementCount == 0) {
         throw new Object();
      } else {
         return this.elementData[this.elementCount - 1];
      }
   }

   public void setElementAt(long obj, int index) {
      if (index >= this.elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this.elementCount).toString());
      }

      this.elementData[index] = obj;
   }

   public void removeElementAt(int index) {
      if (index >= this.elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this.elementCount).toString());
      }

      if (index < 0) {
         throw new Object(index);
      }

      int j = this.elementCount - index - 1;
      if (j > 0) {
         System.arraycopy(this.elementData, index + 1, this.elementData, index, j);
      }

      this.elementCount--;
   }

   public void insertElementAt(long obj, int index) {
      int newcount = this.elementCount + 1;
      if (index >= newcount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" > ").append(this.elementCount).toString());
      }

      if (newcount > this.elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      System.arraycopy(this.elementData, index, this.elementData, index + 1, this.elementCount - index);
      this.elementData[index] = obj;
      this.elementCount++;
   }

   public void addElement(long obj) {
      int newcount = this.elementCount + 1;
      if (newcount > this.elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      this.elementData[this.elementCount++] = obj;
   }

   public void addElements(long[] obj, int offset, int len) {
      int newcount = this.elementCount + len;
      if (newcount > this.elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      System.arraycopy(obj, offset, this.elementData, this.elementCount, len);
      this.elementCount = newcount;
   }

   public boolean removeElement(long obj) {
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

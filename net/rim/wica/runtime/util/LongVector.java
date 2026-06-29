package net.rim.wica.runtime.util;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class LongVector implements Persistable {
   protected long[] _elementData;
   protected int _elementCount;
   protected int _capacityIncrement;

   public LongVector(int initialCapacity, int capacityIncrement) {
      if (initialCapacity < 0) {
         throw new Object(((StringBuffer)(new Object("Illegal Capacity: "))).append(initialCapacity).toString());
      }

      this._elementData = new long[initialCapacity];
      this._capacityIncrement = capacityIncrement;
   }

   public LongVector(int initialCapacity) {
      this(initialCapacity, 0);
   }

   public LongVector() {
      this(10);
   }

   public void copyInto(long[] anArray) {
      System.arraycopy(this._elementData, 0, anArray, 0, this._elementCount);
   }

   public long[] toArray() {
      long[] newArray = new long[this._elementCount];
      System.arraycopy(this._elementData, 0, newArray, 0, this._elementCount);
      return newArray;
   }

   public long[] getArray() {
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

   public boolean contains(long elem) {
      return this.indexOf(elem, 0) >= 0;
   }

   public int indexOf(long elem) {
      return this.indexOf(elem, 0);
   }

   public int indexOf(long elem, int index) {
      for (int i = index; i < this._elementCount; i++) {
         if (elem == this._elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(long elem) {
      return this.lastIndexOf(elem, this._elementCount - 1);
   }

   public int lastIndexOf(long elem, int index) {
      if (index >= this._elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this._elementCount).toString());
      }

      for (int i = index; i >= 0; i--) {
         if (elem == this._elementData[i]) {
            return i;
         }
      }

      return -1;
   }

   public long elementAt(int index) {
      if (index >= this._elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this._elementCount).toString());
      }

      try {
         return this._elementData[index];
      } finally {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" < 0").toString());
      }
   }

   public long firstElement() {
      if (this._elementCount == 0) {
         throw new Object();
      } else {
         return this._elementData[0];
      }
   }

   public long lastElement() {
      if (this._elementCount == 0) {
         throw new Object();
      } else {
         return this._elementData[this._elementCount - 1];
      }
   }

   public void setElementAt(long obj, int index) {
      if (index >= this._elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this._elementCount).toString());
      }

      this._elementData[index] = obj;
   }

   public void removeElementAt(int index) {
      if (index >= this._elementCount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" >= ").append(this._elementCount).toString());
      }

      if (index < 0) {
         throw new Object(index);
      }

      int j = this._elementCount - index - 1;
      if (j > 0) {
         System.arraycopy(this._elementData, index + 1, this._elementData, index, j);
      }

      this._elementCount--;
   }

   public void insertElementAt(long obj, int index) {
      int newcount = this._elementCount + 1;
      if (index >= newcount) {
         throw new Object(((StringBuffer)(new Object())).append(index).append(" > ").append(this._elementCount).toString());
      }

      if (newcount > this._elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      System.arraycopy(this._elementData, index, this._elementData, index + 1, this._elementCount - index);
      this._elementData[index] = obj;
      this._elementCount++;
   }

   public void addElement(long obj) {
      this.addElement(obj, true);
   }

   public void addElement(long obj, boolean clone) {
      int newcount = this._elementCount + 1;
      if (newcount > this._elementData.length) {
         this.ensureCapacityHelper(newcount);
      }

      this._elementData[this._elementCount++] = obj;
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

   public void removeElements(int startIndex, int count) {
      int endIndex = startIndex + count;
      if (startIndex < 0) {
         throw new Object(((StringBuffer)(new Object("startIndex "))).append(startIndex).append(" < 0").toString());
      }

      if (startIndex > endIndex) {
         throw new Object(((StringBuffer)(new Object())).append(startIndex).append(" < ").append(endIndex).toString());
      }

      if (endIndex > this._elementCount) {
         throw new Object(((StringBuffer)(new Object("endIndex "))).append(endIndex).append(" >= ").append(this._elementCount).toString());
      }

      if (endIndex < this._elementCount) {
         System.arraycopy(this._elementData, endIndex, this._elementData, startIndex, this._elementCount - endIndex);
      }

      this._elementCount -= count;
   }

   public void removeAllElements() {
      this._elementCount = 0;
   }
}

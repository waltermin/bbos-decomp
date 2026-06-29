package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.util;

import java.util.EmptyStackException;

public class SimpleStack {
   protected Object[] _elementData;
   protected int _elementCount;
   protected int _capacityIncrement;
   private static final int INITIAL_CAPACITY = 15;

   public SimpleStack(int initialCapacity, int capacityIncrement) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Stack Capacity: " + initialCapacity);
      }

      this._elementData = new Object[initialCapacity];
      this._capacityIncrement = capacityIncrement;
   }

   public SimpleStack(int initialCapacity) {
      this(initialCapacity, 0);
   }

   public SimpleStack() {
      this(15);
   }

   public boolean isEmpty() {
      return this._elementCount == 0;
   }

   public synchronized Object peek() {
      if (this._elementCount == 0) {
         throw new EmptyStackException();
      } else {
         return this._elementData[this._elementCount - 1];
      }
   }

   public Object pop() {
      if (this._elementCount == 0) {
         throw new EmptyStackException();
      }

      Object item = this._elementData[--this._elementCount];
      this._elementData[this._elementCount] = null;
      return item;
   }

   public Object push(Object item) {
      int newcount = this._elementCount + 1;
      if (newcount > this._elementData.length) {
         this.ensureCapacity(newcount);
      }

      this._elementData[this._elementCount++] = item;
      return item;
   }

   private void ensureCapacity(int minCapacity) {
      int oldCapacity = this._elementData.length;
      int newCapacity = this._capacityIncrement > 0 ? oldCapacity + this._capacityIncrement : oldCapacity * 2;
      if (newCapacity < minCapacity) {
         newCapacity = minCapacity;
      }

      Object[] newData = new Object[newCapacity];
      System.arraycopy(this._elementData, 0, newData, 0, oldCapacity);
      this._elementData = newData;
   }
}

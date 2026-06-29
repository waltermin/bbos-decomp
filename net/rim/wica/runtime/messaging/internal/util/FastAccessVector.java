package net.rim.wica.runtime.messaging.internal.util;

import java.util.Vector;

public class FastAccessVector extends Vector {
   public FastAccessVector(int initialCapacity, int capacityIncrement) {
   }

   public FastAccessVector(int initialCapacity) {
   }

   public FastAccessVector() {
   }

   public synchronized Object removeFirstElement() {
      Object first = super.elementData[0];
      if (first != null) {
         if (--super.elementCount > 0) {
            System.arraycopy(super.elementData, 1, super.elementData, 0, super.elementCount);
         }

         super.elementData[super.elementCount] = null;
      }

      return first;
   }

   public synchronized Object removeLastElement() {
      if (super.elementCount > 0) {
         super.elementCount--;
         Object last = super.elementData[super.elementCount];
         super.elementData[super.elementCount] = null;
         return last;
      } else {
         return null;
      }
   }
}

package net.rim.device.api.util;

import net.rim.device.api.collection.ReadableList;
import net.rim.vm.Array;

public final class StringPatternContainer implements ReadableList {
   private StringPattern[] _elements;

   final StringPattern[] getElements() {
      return this._elements;
   }

   @Override
   public final Object getAt(int index) {
      return this._elements[index];
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      int tmpCount = this._elements.length;
      if (count > tmpCount - index) {
         count = tmpCount - index;
      }

      if (elements.length < count + destIndex) {
         Array.resize(elements, count + destIndex);
      }

      System.arraycopy(this._elements, index, elements, destIndex, count);
      return count;
   }

   @Override
   public final int getIndex(Object element) {
      return Arrays.getIndex(this._elements, element);
   }

   @Override
   public final int size() {
      return this._elements.length;
   }

   public StringPatternContainer(StringPattern[] elements) {
      this._elements = elements;
   }
}

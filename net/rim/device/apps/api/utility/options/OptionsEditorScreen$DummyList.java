package net.rim.device.apps.api.utility.options;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;

class OptionsEditorScreen$DummyList implements ReadableList, WritableSet {
   private Vector _elements = (Vector)(new Object());

   @Override
   public int size() {
      return this._elements.size();
   }

   @Override
   public Object getAt(int index) {
      return this._elements.elementAt(index);
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      count = Math.min(count, Math.max(0, this._elements.size() - index));
      count = Math.min(count, Math.max(0, elements.length - destIndex));

      int i;
      for (i = 0; i < count; i++) {
         elements[destIndex + i] = this._elements.elementAt(index + i);
      }

      return i;
   }

   @Override
   public int getIndex(Object element) {
      return this._elements.indexOf(element);
   }

   @Override
   public void add(Object element) {
      this._elements.addElement(element);
   }

   @Override
   public boolean contains(Object element) {
      return this._elements.contains(element);
   }

   @Override
   public void remove(Object element) {
      this._elements.removeElement(element);
   }

   @Override
   public void removeAll() {
      this._elements.removeAllElements();
   }
}

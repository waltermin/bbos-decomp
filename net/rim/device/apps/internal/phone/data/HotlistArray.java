package net.rim.device.apps.internal.phone.data;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.Persistable;

final class HotlistArray extends Vector implements Persistable {
   HotlistArray(int initialSize, int growSize) {
   }

   final synchronized void insertAt(Object obj, int index, PhoneListItems items) {
      int size = this.size();
      if (size == items.getCapacity()) {
         this.insertElementAt(obj, index);
         int candidateForDeletion = items.getCandidateIndexForDeletion();
         if (candidateForDeletion >= 0 && candidateForDeletion < size) {
            this.removeElementAt(candidateForDeletion);
            return;
         }
      } else {
         this.insertElementAt(obj, index);
      }
   }

   final synchronized void add(Object obj) {
      this.addElement(obj);
   }

   final synchronized Object add(Object obj, PhoneListItems items) {
      int size = this.size();
      if (size == items.getCapacity()) {
         int candidateForDeletion = items.getCandidateIndexForDeletion();
         Object deletedObject = null;
         if (candidateForDeletion >= 0 && candidateForDeletion < size) {
            deletedObject = super.elementData[candidateForDeletion];
            super.elementData[candidateForDeletion] = obj;
         }

         return deletedObject;
      } else {
         this.addElement(obj);
         return null;
      }
   }

   @Override
   public final synchronized int indexOf(Object obj) {
      for (int i = 0; i < super.elementCount; i++) {
         if (obj.equals(super.elementData[i])) {
            return i;
         }
      }

      return -1;
   }

   private final boolean validIndex(int index) {
      return index >= 0 && index < super.elementCount;
   }

   public final synchronized Object remove(int index) {
      if (!this.validIndex(index)) {
         return null;
      }

      Object itemToRemove = this.elementAt(index);
      this.removeElementAt(index);
      return itemToRemove;
   }

   final synchronized void sort(Comparator comparator) {
      Arrays.sort(super.elementData, 0, this.size(), comparator);
   }
}

package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.Persistable;

final class IntSubstore implements Persistable {
   private BigVector _objects;
   private IntIntHashtable _index = new IntIntHashtable();

   public IntSubstore() {
      this._objects = new BigVector();
   }

   public final boolean containsKey(int key) {
      return this._index.containsKey(key);
   }

   public final void put(int key, Object value) {
      if (this.containsKey(key)) {
         int index = this._index.get(key);
         this._objects.setElementAt(value, index);
      } else {
         this._objects.addElement(value);
         this._index.put(key, this._objects.size() - 1);
      }
   }

   public final void remove(int key) {
      if (this.containsKey(key)) {
         int lastElement = this._objects.size() - 1;
         int elementToRemove = this._index.remove(key);
         if (elementToRemove == lastElement) {
            this._objects.removeElementAt(elementToRemove);
         } else {
            int tempKey = -1;
            IntEnumeration keys = this._index.keys();

            while (keys.hasMoreElements()) {
               tempKey = keys.nextElement();
               if (this._index.get(tempKey) == lastElement) {
                  break;
               }
            }

            this._index.put(tempKey, elementToRemove);
            this._objects.setElementAt(this._objects.elementAt(lastElement), elementToRemove);
            this._objects.removeElementAt(lastElement);
         }
      }
   }

   public final Object get(int key) {
      return !this.containsKey(key) ? null : this._objects.elementAt(this._index.get(key));
   }

   public final IntEnumeration keys() {
      return this._index.keys();
   }

   public final int size() {
      return this._index.size();
   }

   public final void clear() {
      this._objects.removeAll();
      this._index.clear();
   }
}

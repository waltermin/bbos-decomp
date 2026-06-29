package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.vm.Memory;

final class LongSubstore {
   private long _persistenceKey;
   private LongSubstore$SubstoreData _substoreData;

   private LongSubstore() {
   }

   public LongSubstore(long persistenceKey) {
      this._persistenceKey = persistenceKey;
      this.load();
   }

   private final void load() {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(this._persistenceKey);
      Object obj = persistentObject.getContents();
      if (obj instanceof LongSubstore$SubstoreData) {
         this._substoreData = (LongSubstore$SubstoreData)obj;
      } else {
         this._substoreData = new LongSubstore$SubstoreData(null);
      }
   }

   private final void store() {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(this._persistenceKey);
      persistentObject.setContents(this._substoreData, 51);
      persistentObject.commit();
   }

   public final int getFlashSize() {
      return Memory.objectFlashSize(PersistentStore.getPersistentObject(this._persistenceKey));
   }

   public final boolean containsKey(long key) {
      return this._substoreData._index.containsKey(key);
   }

   public final void put(long key, Object value) {
      if (this.containsKey(key)) {
         int index = this._substoreData._index.get(key);
         this._substoreData._objects.setElementAt(value, index);
      } else {
         this._substoreData._objects.addElement(value);
         this._substoreData._index.put(key, this._substoreData._objects.size() - 1);
      }

      this.store();
   }

   public final void remove(long key) {
      if (this.containsKey(key)) {
         LongIntHashtable index = this._substoreData._index;
         int lastElement = this._substoreData._objects.size() - 1;
         int elementToRemove = index.remove(key);
         if (elementToRemove == lastElement) {
            this._substoreData._objects.removeElementAt(elementToRemove);
         } else {
            long tempKey = -1;
            LongEnumeration keys = index.keys();

            while (keys.hasMoreElements()) {
               tempKey = keys.nextElement();
               if (index.get(tempKey) == lastElement) {
                  break;
               }
            }

            index.put(tempKey, elementToRemove);
            this._substoreData._objects.setElementAt(this._substoreData._objects.elementAt(lastElement), elementToRemove);
            this._substoreData._objects.removeElementAt(lastElement);
         }

         this.store();
      }
   }

   public final Object get(long key) {
      return !this.containsKey(key) ? null : this._substoreData._objects.elementAt(this._substoreData._index.get(key));
   }

   public final LongEnumeration keys() {
      return this._substoreData._index.keys();
   }

   public final int size() {
      return this._substoreData._index.size();
   }

   public final void wipe() {
      this._substoreData._index.clear();
      this._substoreData._objects.removeAll();
      PersistentStore.destroyPersistentObject(this._persistenceKey);
   }
}

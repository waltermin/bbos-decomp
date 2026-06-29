package net.rim.device.api.collection.util;

import java.util.Enumeration;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.collection.WritableLongMap;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Persistable;

public final class LongHashtableCollection implements Persistable, ReadableSet, ReadableLongMap, WritableLongMap {
   private LongHashtable _ht = new LongHashtable();

   public final LongEnumeration getKeys() {
      return this._ht.keys();
   }

   @Override
   public final void remove(long key) {
      this._ht.remove(key);
   }

   @Override
   public final void removeAll() {
      this._ht.clear();
   }

   @Override
   public final int size() {
      return this._ht.size();
   }

   @Override
   public final Object get(long key) {
      return this._ht.get(key);
   }

   @Override
   public final long getKey(Object element) {
      return this._ht.getKey(element);
   }

   @Override
   public final boolean contains(long key) {
      return this._ht.containsKey(key);
   }

   @Override
   public final boolean contains(Object element) {
      return this._ht.contains(element);
   }

   @Override
   public final Enumeration getElements() {
      return this._ht.elements();
   }

   @Override
   public final void put(long key, Object element) {
      this._ht.put(key, element);
   }

   @Override
   public final int getElements(Object[] elements) {
      synchronized (this._ht) {
         Enumeration enumeration = this._ht.elements();
         int dest = 0;
         int length = elements.length;

         while (enumeration.hasMoreElements() && dest < length) {
            elements[dest++] = enumeration.nextElement();
         }

         return dest;
      }
   }
}

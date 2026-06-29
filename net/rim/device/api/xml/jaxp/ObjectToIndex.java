package net.rim.device.api.xml.jaxp;

import java.util.Vector;
import net.rim.device.api.util.ToIntHashtable;

class ObjectToIndex {
   private Vector _v = (Vector)(new Object());
   private int _index = 0;
   private int _added = 0;
   private int _sizeAdded = 0;
   private ToIntHashtable _h = (ToIntHashtable)(new Object());
   private static final int NULLINDEX = 65535;

   Object get(int index) {
      return index == 65535 ? null : this._v.elementAt(index);
   }

   int append(Object key) {
      if (key == null) {
         return 65535;
      }

      this._added++;
      int i = this._h.get(key);
      if (i == -1) {
         i = this._index++;
         this._h.put(key, i);
         this._v.addElement(key);
         this._sizeAdded = this._sizeAdded + key.toString().length();
      }

      return i;
   }

   int getIndex(Object key) {
      return this._h.get(key);
   }

   int size() {
      return this._v.size();
   }

   int getNumAdded() {
      return this._added;
   }

   int getSizeAdded() {
      return this._sizeAdded;
   }
}

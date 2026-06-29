package net.rim.device.apps.internal.browser.html;

import java.util.Vector;
import net.rim.device.api.util.ToIntHashtable;

class ObjectToIndex {
   private Vector _v = new Vector();
   private int _index = 0;
   private int _added = 0;
   private int _sizeAdded = 0;
   private ToIntHashtable _h = new ToIntHashtable();
   private static final int NULLINDEX = -1;
   static final int CDATA_INDEX = -2;
   private static String CDATA_STR = "CDATA";

   Object get(int index) {
      if (index == -1) {
         return null;
      } else {
         return index == -2 ? CDATA_STR : this._v.elementAt(index);
      }
   }

   int append(Object key) {
      if (key == null) {
         return -1;
      }

      if (key.equals(CDATA_STR)) {
         return -2;
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

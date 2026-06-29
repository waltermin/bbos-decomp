package net.rim.ecmascript.util;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.ToIntHashtable;

public class IndexHash {
   private Vector _v = new Vector();
   private int _index = 0;
   private ToIntHashtable _h = new ToIntHashtable();

   public int size() {
      return this._v.size();
   }

   public Enumeration elements() {
      return this._v.elements();
   }

   public Object get(int index) {
      return this._v.elementAt(index);
   }

   public int add(Object key) {
      int i = this._h.get(key);
      if (i == -1) {
         i = this._index++;
         this._h.put(key, i);
         this._v.addElement(key);
      }

      return i;
   }

   public int getIndex(Object key) {
      return this._h.get(key);
   }
}

package net.rim.device.apps.internal.browser.html;

import java.util.Hashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.ToIntHashtable;

class NameIdMultimap {
   private ToIntHashtable _singletons = (ToIntHashtable)(new Object());
   private Hashtable _multiple = (Hashtable)(new Object());

   public NameIdMultimap() {
   }

   public synchronized int getSingle(Object key) {
      return this._singletons.get(key);
   }

   public synchronized IntVector getMultiple(Object key) {
      return (IntVector)this._multiple.get(key);
   }

   public synchronized void put(Object key, int value) {
      IntVector items = (IntVector)this._multiple.get(key);
      if (items != null) {
         items.addElement(value);
      } else {
         int item = this._singletons.get(key);
         if (item == -1) {
            this._singletons.put(key, value);
         } else {
            this._singletons.remove(key);
            IntVector v = (IntVector)(new Object());
            v.addElement(item);
            v.addElement(value);
            this._multiple.put(key, v);
         }
      }
   }
}

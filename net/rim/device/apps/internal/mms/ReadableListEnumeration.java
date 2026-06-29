package net.rim.device.apps.internal.mms;

import java.util.Enumeration;
import net.rim.device.api.collection.ReadableList;

class ReadableListEnumeration implements Enumeration {
   private ReadableList _list;
   private int _index;

   public ReadableListEnumeration(ReadableList list) {
      this._list = list;
   }

   @Override
   public boolean hasMoreElements() {
      return this._index < this._list.size();
   }

   @Override
   public Object nextElement() {
      return this._list.getAt(this._index++);
   }
}

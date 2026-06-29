package net.rim.ecmascript.runtime;

import java.util.Vector;

class ObjectEnumerator extends ESObject {
   private Vector _v = new Vector();
   private int _index;
   private int _size;

   ObjectEnumerator(ESObject obj) {
      obj.enumerate(this._v);
      this._size = this._v.size();
      this._index = 0;
   }

   boolean hasNextElement() {
      return this._index < this._size;
   }

   Object nextElement() {
      return this._v.elementAt(this._index++);
   }
}

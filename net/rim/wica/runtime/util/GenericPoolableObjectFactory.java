package net.rim.wica.runtime.util;

import java.util.Vector;

public class GenericPoolableObjectFactory {
   private Vector _pool;
   private int _maxSize;

   public GenericPoolableObjectFactory(int maxSize) {
      this._maxSize = maxSize;
      this._pool = new Vector(maxSize);
   }

   public Object getInstance() {
      int size = this._pool.size();
      if (size != 0) {
         Object obj = this._pool.lastElement();
         this._pool.removeElementAt(size - 1);
         return obj;
      } else {
         return this.create();
      }
   }

   public void returnBack(Object obj) {
      if (this._pool.size() <= this._maxSize) {
         if (obj instanceof Poolable) {
            ((Poolable)obj).clear();
         }

         this._pool.addElement(obj);
      }
   }

   protected Object create() {
      throw null;
   }
}

package net.rim.tools.compiler.codfile;

import java.util.Hashtable;

public class CodfileVectorHash extends CodfileVector {
   protected Hashtable _table;

   public CodfileVectorHash(int initialCapacity) {
      super(1, false);
      this._table = (Hashtable)(new Object(initialCapacity));
   }

   public CodfileItem get(Object obj) {
      return (CodfileItem)this._table.get(obj);
   }

   public void inject(Object obj, CodfileItem item) {
      this._table.put(obj, item);
   }

   public void put(Object obj, CodfileItem item) {
      this.inject(obj, item);
      this.addElementOrdered(item);
   }
}

package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.Persistable;

public class PersistableObject implements Persistable {
   private Object _obj;

   public PersistableObject(Object obj) {
      this._obj = obj;
   }

   public Object getObject() {
      return this._obj;
   }
}

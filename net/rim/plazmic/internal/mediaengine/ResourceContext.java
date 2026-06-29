package net.rim.plazmic.internal.mediaengine;

import java.util.Hashtable;

public class ResourceContext {
   private Hashtable _table;
   public static final String MEDIA = "Media";
   public static final String HANDLE = "Handle";

   private ResourceContext() {
   }

   public static ResourceContext createContext() {
      return new ResourceContext();
   }

   public Object get(Object key) {
      return this._table == null ? null : this._table.get(key);
   }

   public void set(Object key, Object value) {
      if (this._table == null) {
         this._table = (Hashtable)(new Object());
      }

      this._table.put(key, value);
   }
}

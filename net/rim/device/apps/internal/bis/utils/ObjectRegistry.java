package net.rim.device.apps.internal.bis.utils;

import java.util.Hashtable;

public final class ObjectRegistry {
   private Hashtable _objectMap = new Hashtable();
   private static ObjectRegistry _instance = new ObjectRegistry();

   private ObjectRegistry() {
   }

   public static final ObjectRegistry getInstance() {
      return _instance;
   }

   public final synchronized void register(String name, Object obj) {
      if (null != name && null != obj) {
         this._objectMap.put(name, obj);
      }
   }

   public final synchronized Object find(String name) {
      return this._objectMap.get(name);
   }
}

package net.rim.device.internal.system;

import java.util.Hashtable;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.vm.Persistable;

public class CodeModuleGroupProperties extends Hashtable implements Persistable, SyncObject {
   private int _uid;

   public Object put(String key, String value) {
      Object encodedValue = PersistentContent.encode(value);
      return super.put(key, encodedValue);
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   public CodeModuleGroupProperties(int uid) {
      this._uid = uid;
   }

   @Override
   public Object get(Object key) {
      Object encodedValue = super.get(key);
      String value = null;

      try {
         return PersistentContent.decodeString(encodedValue);
      } catch (ClassCastException cce) {
         return encodedValue;
      }
   }
}

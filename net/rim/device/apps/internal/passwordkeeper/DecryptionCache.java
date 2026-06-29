package net.rim.device.apps.internal.passwordkeeper;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.util.ByteArray;

public final class DecryptionCache {
   private Hashtable _hashtable = (Hashtable)(new Object());
   private static final long ID = -7206766548996023071L;

   private DecryptionCache() {
   }

   public static final DecryptionCache getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      DecryptionCache cache = (DecryptionCache)registry.get(-7206766548996023071L);
      if (cache == null) {
         cache = new DecryptionCache();
         registry.put(-7206766548996023071L, cache);
      }

      return cache;
   }

   public final void add(byte[] encoded, String decoded) {
      ByteArray byteArray = (ByteArray)(new Object(encoded));
      this._hashtable.put(byteArray, decoded);
   }

   public final String get(byte[] encoded) {
      ByteArray byteArray = (ByteArray)(new Object(encoded));
      return (String)this._hashtable.get(byteArray);
   }

   public final void flush() {
      this._hashtable.clear();
   }
}

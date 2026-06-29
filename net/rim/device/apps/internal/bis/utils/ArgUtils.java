package net.rim.device.apps.internal.bis.utils;

import java.util.Hashtable;

public final class ArgUtils {
   public static final String getStringValue(Hashtable hashtable, Object key) {
      return (String)hashtable.get(key);
   }

   public static final Boolean getBooleanValue(Hashtable hashtable, Object key) {
      return (Boolean)hashtable.get(key);
   }
}

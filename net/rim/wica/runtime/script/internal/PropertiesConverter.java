package net.rim.wica.runtime.script.internal;

import java.util.Hashtable;

public final class PropertiesConverter {
   public static final Hashtable convert(byte[] file) {
      Hashtable props = new Hashtable(16);
      String propString = null;

      try {
         propString = new String(file, "UTF-8");
      } finally {
         ;
      }

      int length = propString.length();
      String name = null;
      String value = null;
      int i = 0;

      while (i < length) {
         i = propString.indexOf(61);
         if (i == -1) {
            return props;
         }

         name = propString.substring(0, i);
         name = name.trim();
         propString = propString.substring(i + 1);
         i = propString.indexOf(10);
         if (i == -1) {
            value = propString;
            propString = "";
         } else {
            value = propString.substring(0, i);
            propString = propString.substring(i + 1);
         }

         if (name != null) {
            value = value.trim();
            if (value != null) {
               props.put(name, value);
            }
         }
      }

      return props;
   }
}

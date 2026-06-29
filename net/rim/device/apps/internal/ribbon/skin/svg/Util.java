package net.rim.device.apps.internal.ribbon.skin.svg;

import java.util.Hashtable;
import net.rim.device.api.util.StringTokenizer;

public class Util {
   public static void fillParameters(String query, Hashtable params) {
      params.clear();
      if (query != null) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(query, '&'));

         while (tokenizer.hasMoreTokens()) {
            String nameValuePair = tokenizer.nextToken();
            int equalsIndex = nameValuePair.indexOf(61);
            String name;
            String value;
            if (equalsIndex < 0) {
               name = nameValuePair.trim();
               value = "";
            } else {
               name = nameValuePair.substring(0, equalsIndex).trim();
               value = nameValuePair.substring(equalsIndex + 1).trim();
            }

            if (name.startsWith("amp;")) {
               name = name.substring(4);
            }

            params.put(name, value);
         }
      }
   }
}

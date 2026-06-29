package net.rim.device.api.io;

import net.rim.device.api.util.Comparator;

public final class MIMEMediaComparator implements Comparator {
   private static String WILDCARD = "*";

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof String && o2 instanceof String) {
         String type1 = (String)o1;
         String subtype1 = WILDCARD;
         int index = type1.indexOf("/");
         if (index >= 0) {
            subtype1 = type1.substring(index + 1);
            type1 = type1.substring(0, index);
         }

         String type2 = (String)o2;
         String subtype2 = WILDCARD;
         index = type2.indexOf("/");
         if (index >= 0) {
            subtype2 = type2.substring(index + 1);
            type2 = type2.substring(0, index);
         }

         return !type1.equals(type2) && !WILDCARD.equals(type1) && !WILDCARD.equals(type2)
               || !subtype1.equals(subtype2) && !WILDCARD.equals(subtype1) && !WILDCARD.equals(subtype2)
            ? 1
            : 0;
      } else {
         throw new ClassCastException();
      }
   }
}

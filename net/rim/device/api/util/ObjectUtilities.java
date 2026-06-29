package net.rim.device.api.util;

public final class ObjectUtilities {
   private ObjectUtilities() {
   }

   public static final boolean objEqual(Object o1, Object o2) {
      if (o1 != null) {
         if (o2 != null) {
            return o1.equals(o2);
         }
      } else if (o2 == null) {
         return true;
      }

      return false;
   }

   public static final boolean classesEqual(Object o1, Object o2) {
      if (o1 == o2) {
         return true;
      }

      if (o1 != null && o2 != null) {
         Class c1 = o1.getClass();
         Class c2 = o2.getClass();
         if (c1 == c2) {
            return true;
         }

         String n1 = c1.getName();
         String n2 = c2.getName();
         return n1.equals(n2);
      } else {
         return false;
      }
   }
}

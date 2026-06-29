package net.rim.wica.runtime.util;

import net.rim.device.api.util.Comparator;

public class GenericComparator implements Comparator {
   public static final int EQUALS;
   public static final int LESS_THAN;
   public static final int GREATER_THAN;
   public static final int UNDEFINED;

   @Override
   public int compare(Object ob1, Object ob2) {
      if (ob1 == null) {
         return ob2 == null ? 0 : -1;
      }

      if (ob2 == null) {
         return 1;
      }

      if (!(ob1 instanceof Object)) {
         if (!(ob1 instanceof Object)) {
            if (!(ob1 instanceof Object)) {
               if (!(ob1 instanceof Object)) {
                  if (!(ob1 instanceof Object)) {
                     if (!(ob1 instanceof Object)) {
                        return ob1.equals(ob2) ? 0 : Integer.MAX_VALUE;
                     } else {
                        float float1 = ob1;
                        float float2 = ob2;
                        if (float1 == float2) {
                           return 0;
                        } else {
                           return float1 < float2 ? -1 : 1;
                        }
                     }
                  } else {
                     double double1 = ob1;
                     double double2 = ob2;
                     if (Double.doubleToLongBits(double1) == Double.doubleToLongBits(double2)) {
                        return 0;
                     } else {
                        return double1 < double2 ? -1 : 1;
                     }
                  }
               } else {
                  boolean b1 = ob1;
                  boolean b2 = ob2;
                  if (b1 == b2) {
                     return 0;
                  } else {
                     return !b1 ? -1 : 1;
                  }
               }
            } else {
               long long1 = ob1;
               long long2 = ob2;
               if (long1 == long2) {
                  return 0;
               } else {
                  return long1 < long2 ? -1 : 1;
               }
            }
         } else {
            int result = ((String)ob1).compareTo((String)ob2);
            if (result == 0) {
               return 0;
            } else {
               return result < 0 ? -1 : 1;
            }
         }
      } else {
         long int1 = ((Integer)ob1).intValue();
         long int2 = ((Integer)ob2).intValue();
         if (int1 == int2) {
            return 0;
         } else {
            return int1 < int2 ? -1 : 1;
         }
      }
   }
}

package net.rim.device.api.ldap;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

public class LDAPComparator implements Comparator {
   private String[] _sortFields;

   public String[] getSortFields() {
      return this._sortFields;
   }

   public LDAPComparator(String[] sortAttributes) {
      if (sortAttributes == null) {
         throw new Object();
      }

      this._sortFields = sortAttributes;
   }

   @Override
   public int compare(Object o1, Object o2) {
      for (int i = 0; i < this._sortFields.length; i++) {
         LDAPAttribute a1 = null;
         LDAPAttribute a2 = null;

         try {
            a1 = ((LDAPEntry)o1).getAttribute(this._sortFields[i]);
         } catch (LDAPNoSuchAttributeException var15) {
         }

         try {
            a2 = ((LDAPEntry)o2).getAttribute(this._sortFields[i]);
         } catch (LDAPNoSuchAttributeException var14) {
         }

         if (a1 == null && a2 != null) {
            return -1;
         }

         if (a1 != null && a2 == null) {
            return 1;
         }

         if (a1 != null && a2 != null) {
            int size1 = a1.getSize();
            int size2 = a2.getSize();
            if (size1 < size2) {
               return -1;
            }

            if (size1 > size2) {
               return 1;
            }

            for (int j = 0; j < size1; j++) {
               Object t1 = a1.getValue(j);
               Object t2 = a2.getValue(j);
               if (t1 instanceof Object) {
                  String s1 = (String)t1;
                  if (t2 instanceof Object) {
                     String s2 = (String)t2;
                     int testval = s1.compareTo(s2);
                     if (testval != 0) {
                        return testval;
                     }
                  }
               }
            }
         }
      }

      return 0;
   }

   @Override
   public boolean equals(Object o1) {
      if (this == o1) {
         return true;
      }

      if (o1 instanceof LDAPComparator) {
         LDAPComparator other = (LDAPComparator)o1;
         if (other._sortFields.length != this._sortFields.length) {
            return false;
         }

         if (Arrays.equals(this._sortFields, other._sortFields)) {
            return true;
         }
      }

      return false;
   }
}

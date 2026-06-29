package net.rim.blackberry.api.pim;

import net.rim.device.api.util.Comparator;

class EventListImpl$_eventComparator implements Comparator {
   public boolean matches(Event m, Event e) {
      int[] fields = m.getFields();

      for (int i = 0; i < fields.length; i++) {
         int field = fields[i];
         if (e.countValues(field) <= 0) {
            return false;
         }

         switch (field) {
            case 100:
            case 20000929:
               if (e.getInt(field, 0) != m.getInt(field, 0)) {
                  return false;
               }
               break;
            case 102:
            case 106:
               if (e.getDate(field, 0) != m.getDate(field, 0)) {
                  return false;
               }
               break;
            case 103:
            case 104:
            case 107:
            case 108:
            case 20000927:
               if (!this.matchString(e.getString(field, 0), m.getString(field, 0))) {
                  return false;
               }
               break;
            case 20000928:
               if (e.getBoolean(field, 0) != m.getBoolean(field, 0)) {
                  return false;
               }
         }
      }

      RepeatRule mRecur = m.getRepeat();
      RepeatRule eRecur = e.getRepeat();
      if (mRecur == null && eRecur == null) {
         return true;
      }

      if (mRecur != null && eRecur != null) {
         int[] recurFields = mRecur.getFields();
         int length = recurFields.length;
         if (length != eRecur.getFields().length) {
            return false;
         }

         for (int i = 0; i < length; i++) {
            try {
               if (mRecur.getInt(recurFields[i]) != eRecur.getInt(recurFields[i])) {
                  return false;
               }
            } catch (FieldEmptyException e2) {
               return false;
            }
         }

         long[] d1 = mRecur.getExceptDates();
         long[] d2 = eRecur.getExceptDates();

         for (int i = 0; i < d1.length; i++) {
            long timeDiff = d1[i] - d2[i];
            if (timeDiff < 0) {
               timeDiff *= -1;
            }

            if (timeDiff > 1000) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int compare(Object o1, Object o2) {
      Event e1 = (Event)o1;
      Event e2 = (Event)o2;
      return (int)(e1.getDate(106, 0) - e2.getDate(106, 0));
   }

   @Override
   public boolean equals(Object o) {
      return this == o || o instanceof EventListImpl$_eventComparator;
   }

   private boolean matchString(String str, String prefix) {
      if (str == null && prefix == null) {
         return true;
      }

      if (str != null && prefix != null) {
         str = str.toLowerCase();
         prefix = prefix.toLowerCase();
         int index = 0;

         while (index != -1) {
            if (str.startsWith(prefix, index)) {
               return true;
            }

            index = str.indexOf(32, index);
            if (index >= 0) {
               index++;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}

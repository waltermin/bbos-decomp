package net.rim.device.api.ui;

import net.rim.device.api.util.Comparator;

class FontRegistry$1 implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      return ((String)o1).compareTo((String)o2);
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj;
   }
}

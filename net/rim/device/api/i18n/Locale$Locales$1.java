package net.rim.device.api.i18n;

import net.rim.device.api.util.Comparator;

class Locale$Locales$1 implements Comparator {
   private final Locale$Locales this$0;

   Locale$Locales$1(Locale$Locales _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      Locale l1 = (Locale)o1;
      Locale l2 = (Locale)o2;
      int diff = l1.getCode() - l2.getCode();
      if (diff == 0 && (l1.getVariant() != Locale.EMPTY || l2.getVariant() != Locale.EMPTY)) {
         diff = l1.getVariant().compareTo(l2.getVariant());
      }

      return diff;
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }
}

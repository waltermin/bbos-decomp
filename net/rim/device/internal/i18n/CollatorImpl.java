package net.rim.device.internal.i18n;

public final class CollatorImpl {
   private int _decomposition;
   private int _strength;

   public final int compare(String string1, String string2) {
      if (string1 == null) {
         return string2 == null ? 0 : -1;
      } else {
         return string2 == null ? 1 : this.compare(string1, string2, Integer.MAX_VALUE);
      }
   }

   public final int compare(Object o1, Object o2) {
      if (o1 == null) {
         return o2 == null ? 0 : -1;
      } else {
         return o2 == null ? 1 : this.compare(o1.toString(), o2.toString(), Integer.MAX_VALUE);
      }
   }

   public final native int compare(String var1, String var2, int var3);

   public final boolean equals(String string1, String string2) {
      return this.compare(string1, string2) == 0;
   }
}

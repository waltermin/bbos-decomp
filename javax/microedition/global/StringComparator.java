package javax.microedition.global;

import net.rim.device.api.util.Arrays;

public final class StringComparator {
   private int _level;
   private String _locale;
   public static final int IDENTICAL = 15;
   public static final int LEVEL1 = 1;
   public static final int LEVEL2 = 2;
   public static final int LEVEL3 = 3;

   public StringComparator() {
      this(System.getProperty("microedition.locale"), 1);
   }

   public StringComparator(String locale) {
      this(locale, 1);
   }

   public StringComparator(String locale, int level) {
      if (level >= 1 && (level <= 3 || level == 15)) {
         this._level = level;
         if (locale != null && !locale.equals("")) {
            this._locale = GlobalUtilities.convertUnderscoreToHyphens(locale);
            if (!GlobalUtilities.isValidLocale(this._locale)) {
               throw new IllegalArgumentException();
            }

            if (!this.isSupportedLocale(this._locale)) {
               throw new UnsupportedLocaleException();
            }
         } else {
            this._locale = null;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int compare(String s1, String s2) {
      if (s1 == null || s2 == null) {
         throw new NullPointerException();
      }

      if (this._locale == null) {
         return s1.compareTo(s2);
      }

      switch (this._level) {
         case 3:
            int var5 = this.compare(s1, s2, 1);
            if (var5 == 0) {
               var5 = this.compare(s1, s2, 2);
               if (var5 == 0) {
                  var5 = this.compare(s1, s2, 3);
               }
            }

            return var5;
         case 15:
            int result = this.compare(s1, s2, 15);
         case 2:
            int var4 = this.compare(s1, s2, 1);
            if (var4 == 0) {
               var4 = this.compare(s1, s2, 2);
            }

            return var4;
         default:
            return this.compare(s1, s2, 1);
      }
   }

   private final int compare(String s1, String s2, int level) {
      switch (this._level) {
         default:
            return s1.compareTo(s2);
      }
   }

   public final boolean equals(String s1, String s2) {
      return this.compare(s1, s2) == 0;
   }

   public final int getLevel() {
      return this._level;
   }

   public final String getLocale() {
      return this._locale;
   }

   public static final String[] getSupportedLocales() {
      return new String[0];
   }

   private final boolean isSupportedLocale(String locale) {
      return Arrays.contains(getSupportedLocales(), locale);
   }
}

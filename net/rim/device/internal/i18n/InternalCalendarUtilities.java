package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.ResourceBundle;

public final class InternalCalendarUtilities {
   public static final int getFirstDayOfWeek() {
      ResourceBundle bundle = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale");
      int firstDayOfWeek = Integer.parseInt(bundle.getString(101));
      if (firstDayOfWeek >= 1 && 7 >= firstDayOfWeek) {
         return firstDayOfWeek;
      } else {
         throw new IllegalStateException("Invalid first day of week.");
      }
   }

   public static final boolean is24Hour() {
      return DateTimeFormatOptions.getTimeFormat() == 1;
   }
}

package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.component.DateField;

final class DateTimeController$NetworkTimeField extends DateField {
   public DateTimeController$NetworkTimeField(String label, long date, DateFormat format) {
      super(label, date, format);
   }

   @Override
   public final boolean isFocusable() {
      return false;
   }
}

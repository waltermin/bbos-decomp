package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.internal.ui.Border;

final class HTMLButtonInputField extends HTMLButtonField {
   private static String DEFAULT_VALUE = "";

   HTMLButtonInputField(HTMLInput input, String value, long style, int width, int height, int backgroundColor, int foregroundColor, Border parentBorder) {
      super(input, value != null && value.length() != 0 ? value : DEFAULT_VALUE, style, width, height, backgroundColor, foregroundColor, parentBorder);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         this.doClickAction(true);
      }
   }
}

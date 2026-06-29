package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.DateFormatter;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface EditControl extends UIControl, DateFormatter {
   int TYPE_TEXT;
   int TYPE_NUMBER;
   int TYPE_CURRENCY;
   int TYPE_DATE;
   int TYPE_TIME;
   int TYPE_PERCENTAGE;
   int TYPE_URL;
   int TYPE_PASSWORD;
   int TYPE_PHONE;
   int TYPE_EMAIL;
   int TYPE_DURATION;
   int TYPE_DATE_TIME;

   boolean getBorder();

   int getEditType();

   String getFormat();

   boolean validateText(String var1);
}

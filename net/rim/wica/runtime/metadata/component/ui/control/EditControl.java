package net.rim.wica.runtime.metadata.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.DateFormatter;
import net.rim.wica.runtime.metadata.component.ui.UIControl;

public interface EditControl extends UIControl, DateFormatter {
   int TYPE_TEXT = 0;
   int TYPE_NUMBER = 1;
   int TYPE_CURRENCY = 2;
   int TYPE_DATE = 3;
   int TYPE_TIME = 4;
   int TYPE_PERCENTAGE = 5;
   int TYPE_URL = 6;
   int TYPE_PASSWORD = 7;
   int TYPE_PHONE = 8;
   int TYPE_EMAIL = 9;
   int TYPE_DURATION = 10;
   int TYPE_DATE_TIME = 11;

   boolean getBorder();

   int getEditType();

   String getFormat();

   boolean validateText(String var1);
}

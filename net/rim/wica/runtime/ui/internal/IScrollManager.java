package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;

public interface IScrollManager {
   int getVerticalScroll();

   int getVirtualHeight();

   int getContentHeight();

   void setVerticalScroll(int var1);

   void transformToAppScreen(Field var1, XYRect var2);

   void disableFocusHolder();

   void setLastFocus(Field var1);

   void enableFocusHolder();

   void setFocusHolder();

   int getFieldCount();

   Field getField(int var1);

   Manager castToManager();

   Font getFont();
}

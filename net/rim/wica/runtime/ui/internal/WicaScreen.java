package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Menu;

public interface WicaScreen {
   boolean isFieldVisible(Field var1);

   int getMaxScreenContentHeight();

   void populateMenu(Menu var1, int var2);

   void setTitle(String var1);

   Manager getScrollingManager();
}

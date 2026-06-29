package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Graphics;

public interface ListFieldCallback {
   void drawListRow(ListField var1, Graphics var2, int var3, int var4, int var5);

   int getPreferredWidth(ListField var1);

   Object get(ListField var1, int var2);

   int indexOfList(ListField var1, String var2, int var3);
}

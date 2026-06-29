package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Graphics;

public interface VariableHeightListFieldCallback {
   void drawListRow(VariableHeightListField var1, Graphics var2, int var3, int var4, int var5);

   int getPreferredWidth(VariableHeightListField var1);

   Object get(VariableHeightListField var1, int var2);

   int indexOfList(VariableHeightListField var1, String var2, int var3);

   int getRowHeight(VariableHeightListField var1, int var2);
}

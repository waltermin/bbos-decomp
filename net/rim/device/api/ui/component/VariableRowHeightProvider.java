package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Font;

public interface VariableRowHeightProvider {
   long KEY;

   int getAdjustedY(Font var1, String var2, int var3);

   int getAdjustedY(Font var1, StringBuffer var2, int var3, int var4, int var5);

   int getAdjustedY(int var1);
}

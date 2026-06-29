package net.rim.device.api.lbs;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;

public interface MapField$Implementation {
   void setExtent(int var1, int var2);

   void updateView(int var1, int var2, float var3, int var4);

   void paint(Graphics var1);

   void convertWorldToField(XYPoint var1, boolean var2);

   void convertFieldToWorld(XYPoint var1, boolean var2);
}

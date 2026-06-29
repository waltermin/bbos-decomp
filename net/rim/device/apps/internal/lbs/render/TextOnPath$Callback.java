package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;

public interface TextOnPath$Callback {
   boolean getNextPoint(XYPoint var1);

   int getNextChar();

   int testForCollision(int var1, int var2, int var3);

   void setCollision();

   boolean isMarker();

   int getMarkerWidth();

   void drawMarker(Graphics var1, int var2, int var3);
}

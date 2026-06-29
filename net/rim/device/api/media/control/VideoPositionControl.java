package net.rim.device.api.media.control;

import net.rim.device.api.ui.XYRect;

public interface VideoPositionControl {
   void offset(int var1, int var2);

   void setPosition(XYRect var1);

   void setOnScreen(boolean var1);
}

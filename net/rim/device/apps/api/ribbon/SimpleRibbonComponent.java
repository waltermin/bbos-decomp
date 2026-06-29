package net.rim.device.apps.api.ribbon;

import net.rim.device.api.ui.Graphics;

public interface SimpleRibbonComponent extends RibbonComponent {
   void applyTheme();

   int paintComponent(Graphics var1, int var2, int var3, int var4, int var5, Object var6);

   void setDimensionsAvailable(int var1, int var2);

   int getComponentWidth();

   int getComponentHeight();
}

package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.device.api.ui.XYEdges;

public interface Pannable {
   void setPanX(int var1);

   void setPanY(int var1);

   int getPanX();

   int getPanY();

   boolean isPannable();

   void getPanBounds(XYEdges var1);
}

package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.ui.Graphics;

public interface PreviewViewable {
   int getClientVirtualHeight();

   int getClientHeight();

   int getClientVerticalScroll();

   int getClientVirtualWidth();

   int getClientWidth();

   int getClientHorizontalScroll();

   void drawThumbnail(Graphics var1, int var2);

   void setClientScroll(int var1, int var2);

   void invalidate();

   void activatePreviewMode();
}

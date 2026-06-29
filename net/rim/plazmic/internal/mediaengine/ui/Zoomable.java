package net.rim.plazmic.internal.mediaengine.ui;

public interface Zoomable {
   void setZoomAmount(int var1);

   int getZoomAmount();

   void setZoomOriginX(int var1);

   void setZoomOriginY(int var1);

   int getZoomOriginX();

   int getZoomOriginY();

   boolean isZoomable();
}

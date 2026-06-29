package net.rim.plazmic.internal.mediaengine.service.node;

public interface ViewportNode extends VisualNode {
   int UNITS_PIXELS;
   int UNITS_PERCENT;
   int DEFAULT_UNITS;

   void copyViewbox(int[] var1);

   void setViewbox(int[] var1);

   boolean isClipOverflowVisible();

   void setClipOverflowVisibility(boolean var1);

   int getWidth();

   void setWidth(int var1);

   int getWidthUnits();

   void setWidthUnits(int var1);

   int getHeight();

   void setHeight(int var1);

   int getHeightUnits();

   void setHeightUnits(int var1);

   int getActualWidth();

   void setActualWidth(int var1);

   int getActualHeight();

   void setActualHeight(int var1);

   int getAspectRatio();

   void setAspectRatio(int var1);
}

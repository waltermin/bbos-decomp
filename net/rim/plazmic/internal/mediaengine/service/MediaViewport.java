package net.rim.plazmic.internal.mediaengine.service;

public interface MediaViewport extends MediaService {
   String ID = "Viewport";
   String PME_ID = "PMEViewport";
   int TRANSPARENT = 1;
   int OPTIMIZE_STATIC_BUFFER = 2;
   int OPTIMIZE_VOLATILE_BUFFER = 4;
   int OPTIMIZE_PAN = 8;
   int OPTIMIZE_ZOOM = 16;

   void setStyle(int var1);

   int getStyle();

   void setExtent(int var1, int var2);

   int getOriginX();

   int getOriginY();

   void setOrigin(int var1, int var2);

   boolean isContentWidthAbsolute();

   boolean isContentHeightAbsolute();

   int getVirtualHeight();

   int getVirtualWidth();

   void paint(Object var1);

   void paint(Object var1, int var2, int var3, int var4, int var5);

   void paint(Object var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void invalidate();

   void invalidate(int var1, int var2, int var3, int var4);

   void dirtyAll();

   int getDirtyX();

   int getDirtyY();

   int getDirtyWidth();

   int getDirtyHeight();

   int getAlignX();

   int getAlignY();
}

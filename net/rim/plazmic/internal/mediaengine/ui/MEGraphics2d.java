package net.rim.plazmic.internal.mediaengine.ui;

public interface MEGraphics2d {
   int DRAWSTYLE_ANTIALIAS;

   void getCurrentTransformation(int[] var1, int var2);

   void pushMatrix(int[] var1, int var2);

   void popMatrix();

   void pushViewbox(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   void popViewbox();

   void setPen();

   void setGraphics(Object var1);

   void setStrokeWidth(int var1);

   void setStrokeLinecap(int var1);

   void setStrokeLinejoin(int var1);

   void drawImage(Object var1, int var2, int var3);

   void drawImage(Object var1, int var2, int var3, int var4, int var5);

   void setColor(int var1);

   void setColor(int var1, int var2, int var3);

   void fillRect(int var1, int var2, int var3, int var4);

   void drawRect(int var1, int var2, int var3, int var4);

   void fillEllipse(int var1, int var2, int var3, int var4);

   void fillEllipse(int var1, int var2, int var3, int var4, int var5, int var6);

   void drawEllipse(int var1, int var2, int var3, int var4, int var5, int var6);

   void drawEllipse(int var1, int var2, int var3, int var4);

   void fillPolygon(int var1, int var2, int[] var3, int[] var4);

   void drawPolygon(int var1, int var2, int[] var3, int[] var4);

   void drawLine(int var1, int var2, int var3, int var4);

   void drawText(int var1, String var2, int var3, int var4);

   void drawText(
      char[] var1, int var2, int var3, int var4, int var5, String var6, int var7, int var8, int var9, int var10, int var11, int[] var12, int var13, int var14
   );

   void setAlpha(int var1);

   void setDrawStyle(int var1, boolean var2);

   void fillPath(int var1, int var2, int[] var3, int[] var4, int[] var5, int[] var6, byte[] var7, int[] var8);

   void drawPath(int var1, int var2, int[] var3, int[] var4, int[] var5, int[] var6, byte[] var7, int[] var8, boolean var9);

   void getGraphicsClip(int[] var1, int var2);

   void pushClip(int var1, int var2, int var3, int var4);

   int getFontHeight(int var1);

   int getFontAscent(int var1);

   int getFontAdvance(int var1, String var2);

   int getFontAdvance(char[] var1, int var2, int var3, int var4, String var5, int var6, int var7, int var8, int[] var9, int var10, int var11);

   void popClip();

   void clear(int var1);
}

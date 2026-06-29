package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.device.api.ui.XYRect;

public interface PME12Graphics {
   String ID;
   int INVALID_BUFFER_ID;

   void setGraphics(Object var1);

   XYRect getFullScreenRect();

   void pushContext(MEGraphics2dContext var1);

   MEGraphics2dContext popContext();

   MEGraphics2dContext peekContext();

   void clear(int var1);

   void clear(int var1, int var2, int var3, int var4, int var5);

   void setOffset(int var1, int var2);

   void setBackgroundColor(int var1);

   int createNewBuffer();

   int createNewBuffer(int var1, int var2, boolean var3);

   int getActiveBufferId();

   Object getBuffer(int var1);

   void switchDrawToBuffer(int var1);

   void panBuffer(int var1, int var2, int var3);

   void switchDrawToScreen();

   void applyBuffer(int var1, XYRect var2);

   void applyBuffer(int var1, XYRect var2, int var3, int var4);

   void drawEllipse(int var1, int var2, int var3, int var4);

   void drawImage(Object var1);

   void fillImageBounds(Object var1, XYRect var2);

   void drawPath(int[] var1, int[] var2, int[] var3, int[] var4, byte[] var5, int[] var6, boolean var7);

   void drawRect(int var1, int var2, int var3, int var4);

   void drawText(char[] var1, int var2, int var3);

   void fillTextBounds(char[] var1, int var2, int var3, MEGraphics2dContext var4, int[] var5, XYRect var6);

   void drawForeignObject(ForeignObject var1);

   void drawDirectImage(Object var1, int var2, XYRect var3, int[] var4, int var5, int var6);

   void drawContextList(MEGraphics2dContext[] var1, int var2);

   Object getBitmapObject(Object var1);
}

package net.rim.plazmic.internal.mediaengine.ui;

public interface ForeignObject {
   int getX();

   int getY();

   int getWidth();

   int getHeight();

   void draw(Object var1, int var2, int var3);

   void setPeer(ForeignObjectPeer var1);

   ForeignObjectPeer getPeer();

   Object getInstance();

   void setPosition(int var1, int var2);

   void setExtent(int var1, int var2);

   void setFocus();

   void killFocus();

   boolean isFocusable();

   int getHandle();

   void setHandle(int var1);
}

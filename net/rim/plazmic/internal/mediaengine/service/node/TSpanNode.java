package net.rim.plazmic.internal.mediaengine.service.node;

public interface TSpanNode extends VisualNode, TextAttrNode {
   int TYPE = 32;

   int getStringStart();

   void setStringStart(int var1);

   void setString(char[] var1);

   int getDX();

   void setDX(int var1);

   int getDY();

   void setDY(int var1);
}

package net.rim.plazmic.internal.mediaengine.service.node;

public interface TextNode extends VisualNode, TextAttrNode {
   int TYPE = 50;

   TSpanNode getFirstTSpan();

   void insertFirstTSpan(TSpanNode var1);

   TSpanNode getLastTSpan();

   void insertLastTSpan(TSpanNode var1);

   char[] getString();

   void setString(char[] var1);

   int getWidth();

   void setWidth(int var1);
}

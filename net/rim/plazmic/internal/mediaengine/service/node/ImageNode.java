package net.rim.plazmic.internal.mediaengine.service.node;

public interface ImageNode extends ViewportNode {
   int TYPE = 42;

   Object getImage();

   void setImage(Object var1);
}

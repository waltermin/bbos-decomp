package net.rim.plazmic.internal.mediaengine.service.node;

public interface ImageNode extends ViewportNode {
   int TYPE;

   Object getImage();

   void setImage(Object var1);
}

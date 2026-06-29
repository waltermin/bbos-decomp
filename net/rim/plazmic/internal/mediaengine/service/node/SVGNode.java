package net.rim.plazmic.internal.mediaengine.service.node;

public interface SVGNode extends ViewportNode, ContainerNode {
   int TYPE;

   boolean isZoomAndPannable();

   void setZoomAndPannable(boolean var1);

   void setAsRoot();
}

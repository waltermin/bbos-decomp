package net.rim.plazmic.internal.mediaengine.service.node;

public interface SVGNode extends ViewportNode, ContainerNode {
   int TYPE = 46;

   boolean isZoomAndPannable();

   void setZoomAndPannable(boolean var1);

   void setAsRoot();
}

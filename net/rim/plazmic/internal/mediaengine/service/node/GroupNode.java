package net.rim.plazmic.internal.mediaengine.service.node;

public interface GroupNode extends VisualNode, ContainerNode {
   int SHOW_ALL;
   int SHOW_NONE;
   int TYPE;

   int getCurrentChild();

   void setCurrentChild(int var1);
}

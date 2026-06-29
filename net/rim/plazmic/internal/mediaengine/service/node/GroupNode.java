package net.rim.plazmic.internal.mediaengine.service.node;

public interface GroupNode extends VisualNode, ContainerNode {
   int SHOW_ALL = -2;
   int SHOW_NONE = -1;
   int TYPE = 48;

   int getCurrentChild();

   void setCurrentChild(int var1);
}

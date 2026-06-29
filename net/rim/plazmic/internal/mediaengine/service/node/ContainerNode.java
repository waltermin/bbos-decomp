package net.rim.plazmic.internal.mediaengine.service.node;

public interface ContainerNode extends TextAttrNode {
   Node getFirstChild();

   void insertFirstChild(Node var1);

   Node getLastChild();

   void insertLastChild(Node var1);

   void deleteAllChildren();
}

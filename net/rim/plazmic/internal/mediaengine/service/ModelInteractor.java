package net.rim.plazmic.internal.mediaengine.service;

import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.service.node.ContainerNode;
import net.rim.plazmic.internal.mediaengine.service.node.Node;
import net.rim.plazmic.internal.mediaengine.service.node.SVGNode;
import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;

public interface ModelInteractor extends EventSubscription, MediaModel {
   String ID;

   int getHandle(String var1);

   void trigger(Event var1);

   void trigger(int var1, int var2, Object var3);

   void notify(int var1, int var2, Object var3);

   void initModel(int var1);

   SVGNode getRoot();

   Node getNode(String var1);

   Node getNode(int var1);

   VisualNode createVisualNode(int var1);

   void insertFirstChild(Node var1, ContainerNode var2);

   void insertLastChild(Node var1, ContainerNode var2);

   void insertNextSibling(Node var1, Node var2);

   void insertPreviousSibling(Node var1, Node var2);

   Node removeNode(Node var1);

   Node removeNode(boolean var1, Node var2);

   Node deleteNode(Node var1);

   Node deleteNode(boolean var1, Node var2);
}

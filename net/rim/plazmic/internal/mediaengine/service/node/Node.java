package net.rim.plazmic.internal.mediaengine.service.node;

import net.rim.plazmic.internal.mediaengine.service.ModelInteractor;

public interface Node {
   int NO_HANDLE = -1;

   String getId();

   void setId(String var1);

   ModelInteractor getModel();

   Node getParent();

   Node getNextSibling();

   void insertNextSibling(Node var1);

   Node getPreviousSibling();

   void insertPreviousSibling(Node var1);

   Node removeNode();

   Node removeNode(boolean var1);

   Node deleteNode();

   Node deleteNode(boolean var1);
}

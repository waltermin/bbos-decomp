package net.rim.plazmic.internal.mediaengine.service.interaction;

public interface TreeTraversal {
   String INTERFACE_VISUAL_TREE_TRAVERSAL = "VisualTreeTraversal";

   int getRoot();

   int getParent(int var1);

   int getFirstChild(int var1);

   int getLastChild(int var1);

   int getNextSibling(int var1);

   int getPreviousSibling(int var1);
}

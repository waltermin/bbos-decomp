package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.interaction.TreeTraversal;

public class TreeTraversalImpl implements TreeTraversal {
   private AnimationModel _model;
   private int _root;

   void setTreeData(AnimationModel model, int rootHandle) {
      this._model = model;
      this._root = rootHandle;
   }

   @Override
   public int getRoot() {
      return this._root;
   }

   @Override
   public int getParent(int node) {
      return this._model._nodes[node + 3];
   }

   @Override
   public int getFirstChild(int node) {
      return this._model._nodes[node + 6];
   }

   @Override
   public int getLastChild(int node) {
      return this._model._nodes[node + 7];
   }

   @Override
   public int getNextSibling(int node) {
      return this._model._nodes[node + 4];
   }

   @Override
   public int getPreviousSibling(int node) {
      return this._model._nodes[node + 5];
   }
}

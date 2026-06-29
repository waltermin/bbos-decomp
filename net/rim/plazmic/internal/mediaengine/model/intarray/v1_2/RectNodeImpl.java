package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.RectNode;

public class RectNodeImpl extends VisualNodeImpl implements RectNode {
   public RectNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
   }

   @Override
   public int getWidth() {
      return getWidth(super._handle, super._model);
   }

   public static int getWidth(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 23];
   }

   @Override
   public void setWidth(int width) {
      setWidth(width, super._handle, super._model);
   }

   public static void setWidth(int width, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 23] = width;
      NodeImpl.setDirtyBits(handle, model, 33554432);
   }

   @Override
   public int getHeight() {
      return getHeight(super._handle, super._model);
   }

   public static int getHeight(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 24];
   }

   @Override
   public void setHeight(int height) {
      setHeight(height, super._handle, super._model);
   }

   public static void setHeight(int height, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 24] = height;
      NodeImpl.setDirtyBits(handle, model, 33554432);
   }
}

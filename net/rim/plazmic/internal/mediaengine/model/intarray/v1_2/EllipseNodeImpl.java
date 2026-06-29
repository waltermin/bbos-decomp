package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.EllipseNode;

public class EllipseNodeImpl extends VisualNodeImpl implements EllipseNode {
   public EllipseNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
   }

   @Override
   public int getXRadius() {
      return getXRadius(super._handle, super._model);
   }

   public static int getXRadius(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 23];
   }

   @Override
   public void setXRadius(int radius) {
      setXRadius(radius, super._handle, super._model);
   }

   public static void setXRadius(int radius, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 23] = radius;
      NodeImpl.setDirtyBits(handle, model, 33554432);
   }

   @Override
   public int getYRadius() {
      return getYRadius(super._handle, super._model);
   }

   public static int getYRadius(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 24];
   }

   @Override
   public void setYRadius(int radius) {
      setYRadius(radius, super._handle, super._model);
   }

   public static void setYRadius(int radius, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 24] = radius;
      NodeImpl.setDirtyBits(handle, model, 33554432);
   }
}

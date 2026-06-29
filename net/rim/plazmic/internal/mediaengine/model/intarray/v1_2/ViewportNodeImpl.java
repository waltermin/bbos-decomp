package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.ViewportNode;

public class ViewportNodeImpl extends VisualNodeImpl implements ViewportNode {
   public static void initViewport(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
      model._nodes[handle + 28] = -1;
      model._nodes[handle + 27] = Integer.MAX_VALUE;
   }

   @Override
   public void copyViewbox(int[] array) {
      copyViewbox(array, super._handle, super._model);
   }

   public static void copyViewbox(int[] array, int handle, ModelInteractorImpl model) {
      int viewboxHandle = model._nodes[handle + 28];
      if (viewboxHandle >= 0) {
         System.arraycopy(model._nodes, viewboxHandle + 3, array, 0, 4);
      } else {
         System.arraycopy(
            new int[]{
               -1,
               -1,
               -1,
               -1,
               712179968,
               1411080524,
               2191443,
               -1305840895,
               1398020978,
               16785776,
               -2104615050,
               527827200,
               1816363662,
               1979777255,
               846737962,
               67113293
            },
            0,
            array,
            0,
            4
         );
      }
   }

   @Override
   public void setViewbox(int[] viewbox) {
      setViewbox(viewbox, super._handle, super._model);
   }

   public static void setViewbox(int[] viewbox, int handle, ModelInteractorImpl model) {
      int viewboxHandle = model._nodes[handle + 28];
      if (viewboxHandle < 0) {
         viewboxHandle = model.createNodeHandle(66, 8);
         model._nodes[handle + 28] = viewboxHandle;
      }

      System.arraycopy(viewbox, 0, model._nodes, viewboxHandle + 3, 4);
      NodeImpl.setDirtyBits(handle, model, 167772160);
      model._nodes[handle + 15]++;
   }

   @Override
   public boolean isClipOverflowVisible() {
      return isClipOverflowVisible(super._handle, super._model);
   }

   public static boolean isClipOverflowVisible(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 524288) != 0;
   }

   @Override
   public void setClipOverflowVisibility(boolean visible) {
      setClipOverflowVisibility(visible, super._handle, super._model);
   }

   public static void setClipOverflowVisibility(boolean visible, int handle, ModelInteractorImpl model) {
      if (visible) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 524288;
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -524289;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
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
      model._nodes[handle + 25] = width;
      int bits = 33554432;
      if (NodeImpl.getType(handle, model) == 42) {
         bits |= 134217728;
         model._nodes[handle + 15]++;
      }

      NodeImpl.setDirtyBits(handle, model, bits);
   }

   @Override
   public int getWidthUnits() {
      return getWidthUnits(super._handle, super._model);
   }

   public static int getWidthUnits(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 16384) != 0 ? 2 : 1;
   }

   @Override
   public void setWidthUnits(int units) {
      setWidthUnits(units, super._handle, super._model);
   }

   public static void setWidthUnits(int units, int handle, ModelInteractorImpl model) {
      if (units == 2) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 16384;
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -16385;
      }

      int bits = 33554432;
      if (NodeImpl.getType(handle, model) == 42) {
         bits |= 134217728;
         model._nodes[handle + 15]++;
      }

      NodeImpl.setDirtyBits(handle, model, bits);
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
      model._nodes[handle + 26] = height;
      int bits = 33554432;
      if (NodeImpl.getType(handle, model) == 42) {
         bits |= 134217728;
         model._nodes[handle + 15]++;
      }

      NodeImpl.setDirtyBits(handle, model, bits);
   }

   @Override
   public int getHeightUnits() {
      return getHeightUnits(super._handle, super._model);
   }

   public static int getHeightUnits(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 32768) != 0 ? 2 : 1;
   }

   @Override
   public void setHeightUnits(int units) {
      setHeightUnits(units, super._handle, super._model);
   }

   public static void setHeightUnits(int units, int handle, ModelInteractorImpl model) {
      if (units == 2) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 32768;
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -32769;
      }

      int bits = 33554432;
      if (NodeImpl.getType(handle, model) == 42) {
         bits |= 134217728;
         model._nodes[handle + 15]++;
      }

      NodeImpl.setDirtyBits(handle, model, bits);
   }

   @Override
   public int getActualWidth() {
      return getActualWidth(super._handle, super._model);
   }

   public static int getActualWidth(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 25];
   }

   @Override
   public void setActualWidth(int actualWidth) {
      setActualWidth(actualWidth, super._handle, super._model);
   }

   public static void setActualWidth(int actualWidth, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 25] = actualWidth;
      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public int getActualHeight() {
      return getActualHeight(super._handle, super._model);
   }

   public static int getActualHeight(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 26];
   }

   @Override
   public void setActualHeight(int actualHeight) {
      setActualHeight(actualHeight, super._handle, super._model);
   }

   public static void setActualHeight(int actualHeight, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 26] = actualHeight;
      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public int getAspectRatio() {
      return getAspectRatio(super._handle, super._model);
   }

   public static int getAspectRatio(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 27];
   }

   @Override
   public void setAspectRatio(int aspectRatio) {
      setAspectRatio(aspectRatio, super._handle, super._model);
   }

   public static void setAspectRatio(int aspectRatio, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 27] = aspectRatio;
      NodeImpl.setDirtyBits(handle, model, -16769024);
   }

   static void delete(int handle, ModelInteractorImpl model) {
      int viewboxIndex = model._nodes[handle + 28];
      if (viewboxIndex >= 0) {
         model.deleteNode(viewboxIndex);
      }
   }
}

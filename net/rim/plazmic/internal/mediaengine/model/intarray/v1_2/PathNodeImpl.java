package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.PathNode;

public class PathNodeImpl extends VisualNodeImpl implements PathNode {
   public PathNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
      model._nodes[handle + 23] = -1;
      model._nodes[handle + 24] = -1;
      model._nodes[handle + 25] = -1;
      model._nodes[handle + 26] = -1;
   }

   @Override
   public int[] getXCoordinates() {
      return getXCoordinates(super._handle, super._model);
   }

   public static int[] getXCoordinates(int handle, ModelInteractorImpl model) {
      int index = model._nodes[handle + 23];
      return index >= 0 ? model.getCoordinates(index) : null;
   }

   @Override
   public int[] getFinalXCoordinates() {
      return getFinalXCoordinates(super._handle, super._model);
   }

   public static int[] getFinalXCoordinates(int handle, ModelInteractorImpl model) {
      int index = model._nodes[handle + 23];
      return index >= 0 ? model.getFinalCoordinates(index) : null;
   }

   @Override
   public void setXCoordinates(int[] xCoords) {
      setXCoordinates(xCoords, super._handle, super._model);
   }

   public static void setXCoordinates(int[] xCoords, int handle, ModelInteractorImpl model) {
      int coordsIndex = model._nodes[handle + 23];
      if (coordsIndex >= 0) {
         model.setCoordinates(coordsIndex, xCoords);
      } else {
         coordsIndex = model.addCoordinates(xCoords);
         model._nodes[handle + 23] = coordsIndex;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public int[] getYCoordinates() {
      return getYCoordinates(super._handle, super._model);
   }

   public static int[] getYCoordinates(int handle, ModelInteractorImpl model) {
      int index = model._nodes[handle + 24];
      return index >= 0 ? model.getCoordinates(index) : null;
   }

   @Override
   public int[] getFinalYCoordinates() {
      return getFinalYCoordinates(super._handle, super._model);
   }

   public static int[] getFinalYCoordinates(int handle, ModelInteractorImpl model) {
      int index = model._nodes[handle + 24];
      return index >= 0 ? model.getFinalCoordinates(index) : null;
   }

   @Override
   public void setYCoordinates(int[] yCoords) {
      setYCoordinates(yCoords, super._handle, super._model);
   }

   public static void setYCoordinates(int[] yCoords, int handle, ModelInteractorImpl model) {
      int coordsIndex = model._nodes[handle + 24];
      if (coordsIndex >= 0) {
         model.setCoordinates(coordsIndex, yCoords);
      } else {
         coordsIndex = model.addCoordinates(yCoords);
         model._nodes[handle + 24] = coordsIndex;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public int[] getOffsets() {
      return getOffsets(super._handle, super._model);
   }

   public static int[] getOffsets(int handle, ModelInteractorImpl model) {
      int index = model._nodes[handle + 25];
      return index >= 0 ? model.getCoordinates(index) : null;
   }

   @Override
   public void setOffsets(int[] offsets) {
      setOffsets(offsets, super._handle, super._model);
   }

   public static void setOffsets(int[] offsets, int handle, ModelInteractorImpl model) {
      int offsetsIndex = model._nodes[handle + 25];
      if (offsetsIndex >= 0) {
         model.setCoordinates(offsetsIndex, offsets);
      } else {
         offsetsIndex = model.addCoordinates(offsets);
         model._nodes[handle + 25] = offsetsIndex;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public byte[] getPointTypes() {
      return getPointTypes(super._handle, super._model);
   }

   public static byte[] getPointTypes(int handle, ModelInteractorImpl model) {
      int index = model._nodes[handle + 26];
      return index >= 0 ? model.getPointTypes(index) : null;
   }

   @Override
   public void setPointTypes(byte[] pointTypes) {
      setPointTypes(pointTypes, super._handle, super._model);
   }

   public static void setPointTypes(byte[] pointTypes, int handle, ModelInteractorImpl model) {
      int pointTypesIndex = model._nodes[handle + 26];
      if (pointTypesIndex >= 0) {
         model.setPointTypes(pointTypesIndex, pointTypes);
      } else {
         pointTypesIndex = model.addPointTypes(pointTypes);
         model._nodes[handle + 26] = pointTypesIndex;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   static void delete(int handle, ModelInteractorImpl model) {
      int xCoordsIndex = model._nodes[handle + 23];
      if (xCoordsIndex >= 0) {
         model.removeCoordinates(xCoordsIndex);
      }

      int yCoordsIndex = model._nodes[handle + 24];
      if (yCoordsIndex >= 0) {
         model.removeCoordinates(yCoordsIndex);
      }

      int offsetsIndex = model._nodes[handle + 25];
      if (offsetsIndex >= 0) {
         model.removeCoordinates(offsetsIndex);
      }

      int pointTypesIndex = model._nodes[handle + 26];
      if (pointTypesIndex >= 0) {
         model.removePointTypes(pointTypesIndex);
      }
   }
}

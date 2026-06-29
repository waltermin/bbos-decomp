package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.ForeignObjectNode;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;

public class ForeignObjectNodeImpl extends ViewportNodeImpl implements ForeignObjectNode {
   public ForeignObjectNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      ViewportNodeImpl.initViewport(handle, model);
      model._nodes[handle + 29] = -1;
      model._nodes[handle + 30] = -1;
   }

   @Override
   public ForeignObject getObject() {
      return getObject(super._handle, super._model);
   }

   public static ForeignObject getObject(int handle, ModelInteractorImpl model) {
      int objectIndex = model._nodes[handle + 29];
      return objectIndex >= 0 ? model.getForeignObject(objectIndex) : null;
   }

   @Override
   public void setObject(ForeignObject object) {
      setObject(object, super._handle, super._model);
   }

   public static void setObject(ForeignObject object, int handle, ModelInteractorImpl model) {
      int objectIndex = model._nodes[handle + 29];
      if (objectIndex >= 0) {
         model.setForeignObject(objectIndex, object);
      } else {
         objectIndex = model.addForeignObject(object);
         model._nodes[handle + 29] = objectIndex;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public String getExtension() {
      return getExtension(super._handle, super._model);
   }

   public static String getExtension(int handle, ModelInteractorImpl model) {
      int extensionIndex = model._nodes[handle + 30];
      return extensionIndex >= 0 ? model.getCustomMessage(extensionIndex) : null;
   }

   @Override
   public void setExtension(String extension) {
      setExtension(extension, super._handle, super._model);
   }

   public static void setExtension(String extension, int handle, ModelInteractorImpl model) {
      int extensionIndex = model._nodes[handle + 30];
      if (extensionIndex >= 0) {
         model.setCustomMessage(extensionIndex, extension);
      } else {
         extensionIndex = model.addCustomMessage(extension);
         model._nodes[handle + 30] = extensionIndex;
      }

      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   static void delete(int handle, ModelInteractorImpl model) {
      int foreignObjectIndex = model._nodes[handle + 29];
      if (foreignObjectIndex >= 0) {
         model.removeForeignObject(foreignObjectIndex);
      }

      int extensionIndex = model._nodes[handle + 30];
      if (extensionIndex >= 0) {
         model.removeCustomMessage(extensionIndex);
      }
   }
}

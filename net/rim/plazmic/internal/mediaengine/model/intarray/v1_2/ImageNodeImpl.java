package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.ImageNode;

public class ImageNodeImpl extends ViewportNodeImpl implements ImageNode {
   public ImageNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      ViewportNodeImpl.initViewport(handle, model);
      model._nodes[handle + 29] = -1;
   }

   @Override
   public Object getImage() {
      return getImage(super._handle, super._model);
   }

   public static Object getImage(int handle, ModelInteractorImpl model) {
      int imageIndex = model._nodes[handle + 29];
      return imageIndex >= 0 ? model.getImage(imageIndex) : null;
   }

   @Override
   public void setImage(Object image) {
      setImage(image, super._handle, super._model);
   }

   public static void setImage(Object image, int handle, ModelInteractorImpl model) {
      int imageIndex = model._nodes[handle + 29];
      if (imageIndex >= 0) {
         model.setImage(imageIndex, image);
      } else {
         imageIndex = model.addImage(image);
         model._nodes[handle + 29] = imageIndex;
      }

      NodeImpl.setDirtyBits(handle, model, 16777216);
   }

   static void delete(int handle, ModelInteractorImpl model) {
      int imageIndex = model._nodes[handle + 29];
      if (imageIndex >= 0) {
         model.removeImage(imageIndex);
      }
   }
}
